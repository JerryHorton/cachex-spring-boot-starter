package cn.cug.sxy.shared.cache;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @version 1.0
 * @Date 2025/5/15 23:47
 * @Description Redis 增强器
 * @Author jerryhotton
 */

@Slf4j
@Component
public class RedisEnhancer {

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    private static final String LOCK_PREFIX = "redisEnhancer:lock:";

    private static final String KEY_PREFIX = "redisEnhancer:key:";

    /**
     * 设置缓存（默认策略）
     *
     * @param key   键
     * @param value 值
     * @param <T>   值类型
     */
    public <T> void setValue(String key, T value) {
        String cacheKey = KEY_PREFIX + key;
        redissonClient.<T>getBucket(cacheKey).set(value);
    }

    /**
     * 设置缓存（设置真实TTL）
     *
     * @param key      键
     * @param value    值
     * @param ttl      生存时间
     * @param timeUtil 时间单位
     * @param <T>      值类型
     */
    public <T> void setValue(String key, T value, long ttl, TimeUnit timeUtil) {
        String cacheKey = KEY_PREFIX + key;
        redissonClient.<T>getBucket(cacheKey).set(value, Duration.ofMillis(timeUtil.toMillis(ttl)));
    }

    /**
     * 设置缓存（逻辑过期策略）
     *
     * @param key      键
     * @param value    值
     * @param ttl      生存时间
     * @param timeUtil 时间单位
     * @param <T>      值类型
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
     * 查询缓存（默认策略）
     *
     * @param key 键
     * @param <T> 值类型
     * @return 值
     */
    public <T> T getValue(String key) {
        String cacheKey = KEY_PREFIX + key;
        return redissonClient.<T>getBucket(cacheKey).get();
    }

    /**
     * 查询缓存（互斥锁策略）
     *
     * @param key        键
     * @param dbFallback 数据库回调
     * @param ttl        生存时间
     * @param timeUnit   时间单位
     * @param <T>        值类型
     * @return 值
     */
    public <T> T getValueWithLock(
            String key,
            Supplier<T> dbFallback,
            long ttl,
            TimeUnit timeUnit
    ) {
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

    /**
     * 查询缓存（逻辑过期策略）
     *
     * @param key        键
     * @param dbFallback 数据库回调
     * @param ttl        生存时间
     * @param timeUnit   时间单位
     * @param <T>        值类型
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

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    private static class RedisData {

        private Object data;

        private LocalDateTime expireTime;

    }

}
