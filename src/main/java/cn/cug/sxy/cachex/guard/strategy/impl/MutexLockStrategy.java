package cn.cug.sxy.cachex.guard.strategy.impl;

import cn.cug.sxy.cachex.guard.strategy.AbstractStrategy;
import cn.cug.sxy.cachex.guard.context.CacheContext;
import cn.cug.sxy.cachex.guard.strategy.CacheStrategy;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.time.Duration;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @version 1.0
 * @Date 2025/5/18 15:46
 * @Description 互斥锁策略
 * @Author jerryhotton
 */

public class MutexLockStrategy extends AbstractStrategy {

    private final String KEY_PREFIX = "redisGuard:mutexLock:key:";

    private final String LOCK_PREFIX = "redisGuard:mutexLock:lock:";

    public MutexLockStrategy(RedissonClient redissonClient, ThreadPoolExecutor threadPoolExecutor) {
        super(redissonClient, threadPoolExecutor);
    }

    @Override
    public <T> void set(CacheContext<T> context) {
        String cacheKey = context.getCacheKey();
        Long ttl = context.getTtl();
        TimeUnit timeUnit = context.getTimeUnit();
        setValue(cacheKey, context.getValue(), ttl, timeUnit);
    }

    @Override
    public <T> T get(CacheContext<T> context) {
        String cacheKey = context.getCacheKey();
        Supplier<T> dbQuery = context.getDbQuery();
        Long ttl = context.getTtl();
        TimeUnit timeUnit = context.getTimeUnit();
        return getValueWithLock(cacheKey, dbQuery, ttl, timeUnit);
    }

    @Override
    public CacheStrategy getCacheStrategy() {
        return CacheStrategy.MUTEX_LOCK;
    }

    /**
     * 设置缓存（设置真实TTL）
     *
     * @param key      键
     * @param value    值
     * @param ttl      生存时间
     * @param timeUtil 时间单位
     */
    public <T> void setValue(String key, T value, long ttl, TimeUnit timeUtil) {
        String cacheKey = KEY_PREFIX + key;
        redissonClient.getBucket(cacheKey).set(value, Duration.ofMillis(timeUtil.toMillis(ttl)));
    }

    /**
     * 查询缓存（互斥锁策略）
     *
     * @param key        键
     * @param dbFallback 数据库回调
     * @param ttl        生存时间
     * @param timeUnit   时间单位
     * @return 值
     */
    public <T> T getValueWithLock(String key, Supplier<T> dbFallback, long ttl, TimeUnit timeUnit) {
        String cacheKey = KEY_PREFIX + key;
        T value = redissonClient.<T>getBucket(cacheKey).get();
        // 若缓存中存在，则直接返回
        if (null != value) {
            return value;
        }
        // 若缓存中不存在，则尝试获取锁
        String lockKey = LOCK_PREFIX + key;
        RLock lock = redissonClient.getLock(lockKey);
        try {
            boolean isLocked = lock.tryLock(0, 3, TimeUnit.SECONDS);
            // 若未获取到锁，则等待其他线程从数据库加载数据后再次查询
            if (!isLocked) {
                Thread.sleep(50);
                return getValueWithLock(lockKey, dbFallback, ttl, timeUnit);
            }
            // 双检，再次判断缓存是否写入
            T lastValue = redissonClient.<T>getBucket(cacheKey).get();
            if (null != lastValue) {
                return lastValue;
            }
            // 从数据库加载数据
            T dbValue = dbFallback.get();
            // 数据库命中则将数据写入缓存
            if (null != dbValue) {
                redissonClient.<T>getBucket(cacheKey).set(dbValue, Duration.ofMillis(timeUnit.toMillis(ttl)));
            }
            return dbValue;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

}
