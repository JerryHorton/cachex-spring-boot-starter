package cn.cug.sxy.cachex.guard.strategy;

import cn.cug.sxy.cachex.guard.context.CacheContext;

/**
 * @version 1.0
 * @Date 2025/5/18 15:16
 * @Description 统一策略接口
 * @Author jerryhotton
 */

public interface CacheStrategyHandler {

    <T> void set(CacheContext<T> context);

    <T> T get(CacheContext<T> context);

    CacheStrategy getCacheStrategy();

}
