package cn.cug.sxy.cachex.guard.strategy;

/**
 * @version 1.0
 * @Date 2025/5/18 14:39
 * @Description 策略类型
 * @Author jerryhotton
 */

public enum CacheStrategy {

    DEFAULT,
    MUTEX_LOCK,
    LOGIC_EXPIRE

}
