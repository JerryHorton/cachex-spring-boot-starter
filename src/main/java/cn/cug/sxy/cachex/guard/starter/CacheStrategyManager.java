package cn.cug.sxy.cachex.guard.starter;

import cn.cug.sxy.cachex.guard.strategy.CacheStrategy;
import cn.cug.sxy.cachex.guard.strategy.CacheStrategyHandler;

import java.util.Map;

/**
 * @version 1.0
 * @Date 2025/5/18 17:50
 * @Description 缓存策略管理
 * @Author jerryhotton
 */

public class CacheStrategyManager {

    private final Map<CacheStrategy, CacheStrategyHandler> strategies;

    public CacheStrategyManager(Map<CacheStrategy, CacheStrategyHandler> strategies) {
        this.strategies = strategies;
    }

    public Map<CacheStrategy, CacheStrategyHandler> getAllStrategies() {
        return strategies;
    }

}
