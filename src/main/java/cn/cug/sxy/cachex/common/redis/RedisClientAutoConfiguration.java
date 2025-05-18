package cn.cug.sxy.cachex.common.redis;

import cn.cug.sxy.cachex.common.redis.config.RedisClientConfigProperties;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @version 1.0
 * @Date 2025/2/20 11:15
 * @Description Redis 客户端，使用 Redisson
 * @Author jerryhotton
 */

@Configuration
@EnableConfigurationProperties(RedisClientConfigProperties.class)
public class RedisClientAutoConfiguration {

    @Bean(name = "redissonClient")
    @ConditionalOnMissingBean(RedissonClient.class)
    public RedissonClient redissonClient(RedisClientConfigProperties properties) {
        Config config = new Config();
        config.setCodec(JsonJacksonCodec.INSTANCE);

        config.useSingleServer()
                .setAddress("redis://" + properties.getHost() + ":" + properties.getPort())
                .setConnectionPoolSize(properties.getPoolSize())
                .setConnectionMinimumIdleSize(properties.getMinIdleSize())
                .setIdleConnectionTimeout(properties.getIdleTimeout())
                .setConnectTimeout(properties.getConnectTimeout())
                .setRetryAttempts(properties.getRetryAttempts())
                .setRetryInterval(properties.getRetryInterval())
                .setPingConnectionInterval(properties.getPingInterval())
                .setKeepAlive(properties.isKeepAlive())
        ;

        return Redisson.create(config);
    }

}
