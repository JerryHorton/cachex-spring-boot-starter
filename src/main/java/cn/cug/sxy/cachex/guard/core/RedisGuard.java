package cn.cug.sxy.cachex.guard.core;

import cn.cug.sxy.cachex.guard.starter.CacheStrategyManager;
import cn.cug.sxy.cachex.guard.strategy.CacheStrategy;
import cn.cug.sxy.cachex.guard.strategy.CacheStrategyHandler;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;

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
public class RedisGuard {

    private final RedissonClient redissonClient;

    private final ThreadPoolExecutor threadPoolExecutor;

    private final CacheStrategyManager cacheStrategyManager;

    public RedisGuard(RedissonClient redissonClient, ThreadPoolExecutor threadPoolExecutor, CacheStrategyManager cacheStrategyManager) {
        this.redissonClient = redissonClient;
        this.threadPoolExecutor = threadPoolExecutor;
        this.cacheStrategyManager = cacheStrategyManager;
    }

    public CacheStrategyHandler setStrategy(CacheStrategy strategy) {
        return cacheStrategyManager.getAllStrategies().get(strategy);
    }

}
