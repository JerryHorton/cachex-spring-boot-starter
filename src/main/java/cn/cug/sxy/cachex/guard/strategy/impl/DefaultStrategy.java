package cn.cug.sxy.cachex.guard.strategy.impl;

import cn.cug.sxy.cachex.guard.strategy.AbstractStrategy;
import cn.cug.sxy.cachex.guard.context.CacheContext;
import cn.cug.sxy.cachex.guard.strategy.CacheStrategy;
import org.redisson.api.RedissonClient;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @version 1.0
 * @Date 2025/5/18 15:36
 * @Description 默认策略
 * @Author jerryhotton
 */

public class DefaultStrategy extends AbstractStrategy {

    private final String KEY_PREFIX = "redisGuard:default:key";

    public DefaultStrategy(RedissonClient redissonClient, ThreadPoolExecutor threadPoolExecutor) {
        super(redissonClient, threadPoolExecutor);
    }

    @Override
    public <T> void set(CacheContext<T> context) {
        Object value = context.getValue();
        String cacheKey = context.getCacheKey();
        setValue(cacheKey, value);
    }

    @Override
    public <T> T get(CacheContext<T> context) {
        String cacheKey = context.getCacheKey();
        return getValue(cacheKey);
    }

    @Override
    public CacheStrategy getCacheStrategy() {
        return CacheStrategy.DEFAULT;
    }

    /**
     * 设置缓存（默认策略）
     *
     * @param key   键
     * @param value 值
     */
    public <T> void setValue(String key, T value) {
        String cacheKey = KEY_PREFIX + key;
        redissonClient.getBucket(cacheKey).set(value);
    }

    /**
     * 查询缓存（默认策略）
     *
     * @param key 键
     * @return 值
     */
    public <T> T getValue(String key) {
        String cacheKey = KEY_PREFIX + key;
        return redissonClient.<T>getBucket(cacheKey).get();
    }

}
