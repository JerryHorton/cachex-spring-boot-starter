package cn.cug.sxy.cachex.luascript.starter;

import cn.cug.sxy.cachex.luascript.core.RedissonLuaWrapper;
import cn.cug.sxy.cachex.luascript.model.RedisLuaScriptMapper;
import cn.cug.sxy.cachex.luascript.model.RedisOperation;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;

import java.util.*;
import java.util.function.Consumer;

/**
 * @version 1.0
 * @Date 2025/6/7 20:27
 * @Description Lua脚本管理
 * @Author jerryhotton
 */

public class LuaScriptManager {

    private final RScript rScript;

    public LuaScriptManager(RedissonClient redissonClient) {
        this.rScript = redissonClient.getScript();
    }

    public List<Object> exec(Consumer<RedissonLuaWrapper> operations) {
        List<RedisOperation> operationList = new ArrayList<>();
        // 创建一个包装器来收集操作
        RedissonLuaWrapper wrapper = new RedissonLuaWrapper() {
            @Override
            public RedisOperation set(String key, Object value) {
                RedisOperation operation = super.set(key, value);
                operationList.add(operation);
                return operation;
            }

            @Override
            public RedisOperation setPX(String key, Object value, Object expireTimeMills) {
                RedisOperation operation = super.setPX(key, value, expireTimeMills);
                operationList.add(operation);
                return operation;
            }

            @Override
            public RedisOperation get(String key) {
                RedisOperation operation = super.get(key);
                operationList.add(operation);
                return operation;
            }

            @Override
            public RedisOperation del(String key) {
                RedisOperation operation = super.del(key);
                operationList.add(operation);
                return operation;
            }

            @Override
            public RedisOperation decrBy(String key, Object decrement) {
                RedisOperation operation = super.decrBy(key, decrement);
                operationList.add(operation);
                return operation;
            }

            @Override
            public RedisOperation incr(String key) {
                RedisOperation operation = super.incr(key);
                operationList.add(operation);
                return operation;
            }

            @Override
            public RedisOperation incrBy(String key, Object increment) {
                RedisOperation operation = super.incrBy(key, increment);
                operationList.add(operation);
                return operation;
            }

            @Override
            public RedisOperation zadd(String key, double score, Object member) {
                RedisOperation operation = super.zadd(key, score, member);
                operationList.add(operation);
                return operation;
            }

            @Override
            public RedisOperation zrem(String key, Object member) {
                RedisOperation operation = super.zrem(key, member);
                operationList.add(operation);
                return operation;
            }

            @Override
            public RedisOperation zrange(String key, long start, long end) {
                RedisOperation operation = super.zrange(key, start, end);
                operationList.add(operation);
                return operation;
            }

            @Override
            public RedisOperation zincrby(String key, double increment, Object member) {
                RedisOperation operation = super.zincrby(key, increment, member);
                operationList.add(operation);
                return operation;
            }

            @Override
            public RedisOperation zrangebyscore(String key, double min, double max) {
                RedisOperation operation = super.zrangebyscore(key, min, max);
                operationList.add(operation);
                return operation;
            }

            @Override
            public RedisOperation zremrangebyrank(String key, long start, long end) {
                RedisOperation operation = super.zremrangebyrank(key, start, end);
                operationList.add(operation);
                return operation;
            }

            @Override
            public RedisOperation zremrangebyscore(String key, double min, double max) {
                RedisOperation operation = super.zremrangebyscore(key, min, max);
                operationList.add(operation);
                return operation;
            }

        };
        // 执行用户提供的操作
        operations.accept(wrapper);
        // 执行收集到的操作
        return executeMultipleCommands(operationList);
    }

    private List<Object> executeMultipleCommands(List<RedisOperation> operations) {
        List<Object> allKeys = new ArrayList<>();
        List<Object> allArgs = new ArrayList<>();
        // 构建 Lua 脚本和参数
        StringBuilder luaScript = new StringBuilder();
        luaScript.append("local results = {}; ");
        int ketIndex = 1;
        int argIndex = 1;

        for (RedisOperation operation : operations) {
            RedisLuaScriptMapper operationType = operation.getScriptMapper();
            String key = operation.getKey();
            List<Object> args = operation.getArgs();
            // 确定参数数量
            int[] argIndices = new int[args.size()];
            for (int i = 0; i < args.size(); i++) {
                argIndices[i] = argIndex + i;
            }
            // 格式化脚本，使用正确的参数索引
            String script = operationType.formatScript(ketIndex, argIndices);
            luaScript.append("table.insert(results, ").append(script).append("); ");
            // 收集操作中的 key 和 args
            allKeys.add(key);
            allArgs.addAll(args);
            // 更新索引
            ketIndex += 1;
            argIndex += args.size();
        }

        luaScript.append("return results");
        // 执行 Lua 脚本，返回每个操作的结果
        return rScript.eval(RScript.Mode.READ_WRITE, luaScript.toString(), RScript.ReturnType.MULTI, allKeys, allArgs.toArray());
    }

}
