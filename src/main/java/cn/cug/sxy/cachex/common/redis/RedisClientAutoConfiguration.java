package cn.cug.sxy.config;

import cn.cug.sxy.domain.strategy.model.entity.StockReplenishEntity;
import cn.cug.sxy.trigger.listener.redisTopic.ResultWaitListener;
import cn.cug.sxy.trigger.listener.redisTopic.StockReplenishListener;
import cn.cug.sxy.shared.common.Constants;
import org.redisson.Redisson;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
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
public class RedisClientConfig {

    @Bean("redissonClient")
    public RedissonClient redissonClient(RedisClientConfigProperties properties) {
        Config config = new Config();
        config.setCodec(JsonJacksonCodec.INSTANCE);

        config.useSingleServer()
                .setAddress("redis://" + properties.getHost() + ":" + properties.getPort())
                // .setPassword(properties.getPassword())
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

    @Bean(name = "stockReplenishTopic")
    public RTopic stockReplenishTopic(RedissonClient redissonClient, StockReplenishListener listener) {
        RTopic topic = redissonClient.getTopic(Constants.RedisKey.STRATEGY_AWARD_STOCK_REPLENISH_TOPIC);
        topic.addListener(StockReplenishEntity.class, listener);
        return topic;
    }

    @Bean(name = "resultWaitTopic")
    public RTopic resultWaitTopic(RedissonClient redissonClient, ResultWaitListener listener) {
        RTopic topic = redissonClient.getTopic(Constants.RedisKey.RESULT_WAIT_TOPIC);
        topic.addListener(String.class, listener);
        return topic;
    }

}
