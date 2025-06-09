package cn.cug.sxy.cachex.luascript;

import cn.cug.sxy.cachex.luascript.starter.LuaScriptManager;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;

/**
 * @version 1.0
 * @Date 2025/6/7 23:51
 * @Description Lua脚本管理器自动配置
 * @Author jerryhotton
 */

@AutoConfiguration
@ConditionalOnBean(RedissonClient.class)
public class LuaScriptManagerAutoConfiguration {

    @Bean
    public LuaScriptManager luaScriptManager(RedissonClient redissonClient) {
        return new LuaScriptManager(redissonClient);
    }

}
