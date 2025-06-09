package cn.cug.sxy.cachex.luascript.core;

import cn.cug.sxy.cachex.luascript.model.RedisLuaScriptMapper;
import cn.cug.sxy.cachex.luascript.model.RedisOperation;

import java.util.ArrayList;
import java.util.List;

/**
 * @version 1.0
 * @Date 2025/6/7 21:21
 * @Description Lua 脚本包装
 * @Author jerryhotton
 */

public class RedissonLuaWrapper {

    public RedisOperation set(String key, Object value) {
        List<Object> values = new ArrayList<>();
        values.add(value);
        return new RedisOperation(RedisLuaScriptMapper.SET, key, values);
    }

    public RedisOperation setPX(String key, Object value, Object expireTimeMills) {
        List<Object> values = new ArrayList<>();
        values.add(value);
        values.add(expireTimeMills);
        return new RedisOperation(RedisLuaScriptMapper.SETPX, key, values);
    }

    public RedisOperation get(String key) {
        return new RedisOperation(RedisLuaScriptMapper.GET, key, new ArrayList<>());
    }

    public RedisOperation del(String key) {
        return new RedisOperation(RedisLuaScriptMapper.DEKEY, key, new ArrayList<>());
    }

    public RedisOperation decrBy(String key, Object decrement) {
        List<Object> values = new ArrayList<>();
        values.add(decrement);
        return new RedisOperation(RedisLuaScriptMapper.DECRBY, key, values);
    }

    public RedisOperation incr(String key) {
        return new RedisOperation(RedisLuaScriptMapper.INCR, key, new ArrayList<>());
    }

    public RedisOperation incrBy(String key, Object increment) {
        List<Object> values = new ArrayList<>();
        values.add(increment);
        return new RedisOperation(RedisLuaScriptMapper.INCRBY, key, values);
    }

    public RedisOperation zadd(String key, double score, Object member) {
        List<Object> values = new ArrayList<>();
        values.add(score);
        values.add(member);
        return new RedisOperation(RedisLuaScriptMapper.ZADD, key, values);
    }

    public RedisOperation zrem(String key, Object member) {
        List<Object> values = new ArrayList<>();
        values.add(member);
        return new RedisOperation(RedisLuaScriptMapper.ZREM, key, values);
    }

    public RedisOperation zrange(String key, long start, long end) {
        List<Object> values = new ArrayList<>();
        values.add(start);
        values.add(end);
        return new RedisOperation(RedisLuaScriptMapper.ZRANGE, key, values);
    }

    public RedisOperation zincrby(String key, double increment, Object member) {
        List<Object> values = new ArrayList<>();
        values.add(increment);
        values.add(member);
        return new RedisOperation(RedisLuaScriptMapper.ZINCRBY, key, values);
    }

    public RedisOperation zrangebyscore(String key, double min, double max) {
        List<Object> values = new ArrayList<>();
        values.add(min);
        values.add(max);
        return new RedisOperation(RedisLuaScriptMapper.ZRANGEBYSCORE, key, values);
    }

    public RedisOperation zremrangebyrank(String key, long start, long end) {
        List<Object> values = new ArrayList<>();
        values.add(start);
        values.add(end);
        return new RedisOperation(RedisLuaScriptMapper.ZREMRANGEBYRANK, key, values);
    }

    public RedisOperation zremrangebyscore(String key, double min, double max) {
        List<Object> values = new ArrayList<>();
        values.add(min);
        values.add(max);
        return new RedisOperation(RedisLuaScriptMapper.ZREMRANGEBYSCORE, key, values);
    }

}