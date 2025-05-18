package cn.cug.sxy.cachex.guard.strategy;

import org.redisson.api.RedissonClient;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @version 1.0
 * @Date 2025/5/18 15:38
 * @Description 缓存策略抽象类
 * @Author jerryhotton
 */

public abstract class AbstractStrategy implements CacheStrategyHandler {

    protected final RedissonClient redissonClient;

    protected final ThreadPoolExecutor threadPoolExecutor;

    public AbstractStrategy(RedissonClient redissonClient, ThreadPoolExecutor threadPoolExecutor) {
        this.redissonClient = redissonClient;
        this.threadPoolExecutor = threadPoolExecutor;
    }

}
