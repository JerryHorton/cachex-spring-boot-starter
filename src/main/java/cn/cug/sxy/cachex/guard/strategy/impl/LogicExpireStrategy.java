package cn.cug.sxy.cachex.guard.strategy.impl;

import cn.cug.sxy.cachex.guard.model.RedisData;
import cn.cug.sxy.cachex.guard.strategy.AbstractStrategy;
import cn.cug.sxy.cachex.guard.context.CacheContext;
import cn.cug.sxy.cachex.guard.strategy.CacheStrategy;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @version 1.0
 * @Date 2025/5/18 16:23
 * @Description
 * @Author jerryhotton
 */

@Slf4j
public class LogicExpireStrategy extends AbstractStrategy {

    private final String KEY_PREFIX = "redisGuard:logicExpire:key:";

    private final String LOCK_PREFIX = "redisGuard:logicExpire:lock:";

    public LogicExpireStrategy(RedissonClient redissonClient, ThreadPoolExecutor threadPoolExecutor) {
        super(redissonClient, threadPoolExecutor);
    }

    @Override
    public <T> void set(CacheContext<T> context) {
        String cacheKey = context.getCacheKey();
        Long ttl = context.getTtl();
        TimeUnit timeUnit = context.getTimeUnit();
        setValueWithLogicExpire(cacheKey, context.getValue(), ttl, timeUnit);
    }

    @Override
    public <T> T get(CacheContext<T> context) {
        String cacheKey = context.getCacheKey();
        Supplier<T> dbQuery = context.getDbQuery();
        Long ttl = context.getTtl();
        TimeUnit timeUnit = context.getTimeUnit();
        return getValueWithLogicExpire(cacheKey, dbQuery, ttl, timeUnit);
    }

    @Override
    public CacheStrategy getCacheStrategy() {
        return CacheStrategy.LOGIC_EXPIRE;
    }

    /**
     * 设置缓存（逻辑过期策略）
     *
     * @param key      键
     * @param value    值
     * @param ttl      生存时间
     * @param timeUtil 时间单位
     */
    public <T> void setValueWithLogicExpire(String key, T value, long ttl, TimeUnit timeUtil) {
        String cacheKey = KEY_PREFIX + key;
        RedisData redisData = RedisData.builder()
                .data(value)
                .expireTime(LocalDateTime.now().plus(Duration.ofMillis(timeUtil.toMillis(ttl))))
                .build();
        redissonClient.<String>getBucket(cacheKey).set(JSON.toJSONString(redisData));
    }

    /**
     * 查询缓存（逻辑过期策略）
     *
     * @param key        键
     * @param dbFallback 数据库回调
     * @param ttl        生存时间
     * @param timeUnit   时间单位
     * @return 值
     */
    public <T> T getValueWithLogicExpire(
            String key,
            Supplier<T> dbFallback,
            long ttl,
            TimeUnit timeUnit
    ) {
        String cacheKey = KEY_PREFIX + key;
        String json = redissonClient.<String>getBucket(cacheKey).get();
        // 虽设置了逻辑过期时间，但实际为永久有效。若缓存中不存在，则直接返回
        if (null == json) {
            return null;
        }
        RedisData redisData = JSON.parseObject(json, new TypeReference<RedisData>() {
        }.getType());
        // 解析原始数据
        T oldData = JSON.parseObject(JSON.toJSONString(redisData.getData()), new TypeReference<T>() {
        }.getType());
        // 若没有逻辑过期则直接返回
        if (redisData.getExpireTime().isAfter(LocalDateTime.now())) {
            return oldData;
        }
        // 若逻辑过期，则后台刷新
        String lockKey = LOCK_PREFIX + key;
        RLock lock = redissonClient.getLock(lockKey);
        try {
            boolean isLocked = lock.tryLock(0, 3, TimeUnit.SECONDS);
            if (isLocked) {
                threadPoolExecutor.execute(() -> {
                    try {
                        T newData = dbFallback.get();
                        setValueWithLogicExpire(key, newData, ttl, timeUnit);
                    } catch (Exception e) {
                        log.error("异步刷新缓存失败", e);
                    } finally {
                        if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                            lock.unlock();
                        }
                    }
                });
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
        // 返回旧数据
        return oldData;
    }

}
