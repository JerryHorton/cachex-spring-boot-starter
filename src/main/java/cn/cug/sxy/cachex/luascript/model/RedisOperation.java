package cn.cug.sxy.cachex.luascript.model;

import java.util.List;

/**
 * @version 1.0
 * @Date 2025/6/7 21:23
 * @Description redis操作
 * @Author jerryhotton
 */

public class RedisOperation {

    private RedisLuaScriptMapper scriptMapper;

    private String key;

    private List<Object> args;

    public RedisOperation(RedisLuaScriptMapper scriptMapper, String key, List<Object> args) {
        this.scriptMapper = scriptMapper;
        this.key = key;
        this.args = args;
    }

    public RedisLuaScriptMapper getScriptMapper() {
        return scriptMapper;
    }

    public void setScriptMapper(RedisLuaScriptMapper scriptMapper) {
        this.scriptMapper = scriptMapper;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setArgs(List<Object> args) {
        this.args = args;
    }

    public String getKey() {
        return key;
    }

    public List<Object> getArgs() {
        return args;
    }

}
