package cn.cug.sxy.cachex.luascript.model;

/**
 * @version 1.0
 * @Date 2025/6/7 20:43
 * @Description Lua脚本映射
 * @Author jerryhotton
 */

public enum RedisLuaScriptMapper {

    // zset 操作
    ZADD("zadd", "redis.call('zadd', KEYS[%d], ARGV[%d], ARGV[%d])"),
    ZREM("zrem", "redis.call('zrem', KEYS[%d], ARGV[%d])"),
    ZRANGE("zrange", "redis.call('zrange', KEYS[%d], ARGV[%d], ARGV[%d])"),
    ZINCRBY("zincrby", "redis.call('zincrby', KEYS[%d], ARGV[%d], ARGV[%d])"),
    ZRANGEBYSCORE("zrangebyscore", "redis.call('zrangebyscore', KEYS[%d], ARGV[%d], ARGV[%d])"),
    ZREMRANGEBYRANK("zremrangebyrank", "redis.call('zremrangebyrank', KEYS[%d], ARGV[%d], ARGV[%d])"),
    ZREMRANGEBYSCORE("zremrangebyscore", "redis.call('zremrangebyscore', KEYS[%d], ARGV[%d], ARGV[%d])"),

    // string 操作
    SET("set", "redis.call('set', KEYS[%d], ARGV[%d])"),
    SETPX("setpx", "redis.call('set', KEYS[%d], ARGV[%d], 'PX', tonumber(ARGV[%d]))"),
    GET("get", "redis.call('get', KEYS[%d])"),
    DEKEY("del", "redis.call('del', KEYS[%d])"),
    INCRBY("incrby", "redis.call('incrby', KEYS[%d], ARGV[%d])"),
    INCR("incr", "redis.call('incr', KEYS[%d])"),
    DECRBY("decrby", "redis.call('decrby', KEYS[%d], ARGV[%d])"),
    DECR("decr", "redis.call('decr', KEYS[%d])");


    private final String type;
    private final String scriptTemplate;

    RedisLuaScriptMapper(String type, String scriptTemplate) {
        this.type = type;
        this.scriptTemplate = scriptTemplate;
    }

    public String getType() {
        return type;
    }

    public String getScriptTemplate() {
        return scriptTemplate;
    }

    public String formatScript(int keyIndex, int... argIndices) {
        Object[] indices = new Object[argIndices.length + 1];
        indices[0] = keyIndex;
        for (int i = 0; i < argIndices.length; i++) {
            indices[i + 1] = argIndices[i];
        }
        return String.format(scriptTemplate, indices);
    }

}
