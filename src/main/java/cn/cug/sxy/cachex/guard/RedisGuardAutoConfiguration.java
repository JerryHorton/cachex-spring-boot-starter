package cn.cug.sxy.cachex.guard;

import cn.cug.sxy.cachex.guard.core.RedisConsistency;
import cn.cug.sxy.cachex.guard.core.RedisGuard;
import cn.cug.sxy.cachex.guard.starter.CacheStrategyManager;
import cn.cug.sxy.cachex.guard.strategy.CacheStrategy;
import cn.cug.sxy.cachex.guard.strategy.CacheStrategyHandler;
import cn.cug.sxy.cachex.guard.strategy.impl.DefaultStrategy;
import cn.cug.sxy.cachex.guard.strategy.impl.LogicExpireStrategy;
import cn.cug.sxy.cachex.guard.strategy.impl.MutexLockStrategy;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Date 2025/5/17 23:07
 * @Description 自动配置类
 * @Author jerryhotton
 */

@Configuration
@ConditionalOnBean({RedissonClient.class, ThreadPoolExecutor.class})
public class RedisGuardAutoConfiguration {

    @Bean
    public RedisGuard redisGuard(RedissonClient redissonClient, ThreadPoolExecutor threadPoolExecutor, CacheStrategyManager cacheStrategyManager) {
        return new RedisGuard(redissonClient, threadPoolExecutor, cacheStrategyManager);
    }

    @Bean
    public DefaultStrategy defaultStrategy(RedissonClient redissonClient, ThreadPoolExecutor threadPoolExecutor) {
        return new DefaultStrategy(redissonClient, threadPoolExecutor);
    }

    @Bean
    public MutexLockStrategy mutexLockStrategy(RedissonClient redissonClient, ThreadPoolExecutor threadPoolExecutor) {
        return new MutexLockStrategy(redissonClient, threadPoolExecutor);
    }

    @Bean
    public LogicExpireStrategy logicExpireStrategy(RedissonClient redissonClient, ThreadPoolExecutor threadPoolExecutor) {
        return new LogicExpireStrategy(redissonClient, threadPoolExecutor);
    }

    @Bean
    public CacheStrategyManager cacheStrategyManager(List<CacheStrategyHandler> cacheStrategyHandlers) {
        Map<CacheStrategy, CacheStrategyHandler> strategyHandlerMap = cacheStrategyHandlers.stream().collect(Collectors.toMap(CacheStrategyHandler::getCacheStrategy, handler -> handler));
        return new CacheStrategyManager(strategyHandlerMap);
    }

    @Bean
    public RedisConsistency redisConsistency(RedissonClient redissonClient) {
        return new RedisConsistency(redissonClient);
    }

}
