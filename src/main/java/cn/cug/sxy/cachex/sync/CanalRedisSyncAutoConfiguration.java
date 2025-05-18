package cn.cug.sxy.cachex.sync;

import cn.cug.sxy.cachex.sync.canal.CanalRedisSyncRunner;
import cn.cug.sxy.cachex.sync.config.CanalClientProperties;
import cn.cug.sxy.cachex.sync.core.CanalSyncManager;
import cn.cug.sxy.cachex.sync.core.EntityBuilder;
import cn.cug.sxy.cachex.sync.listener.CanalClientListener;
import cn.cug.sxy.cachex.sync.starter.CanalSyncRegistrar;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @version 1.0
 * @Date 2025/5/16 21:58
 * @Description 自动配置类
 * @Author jerryhotton
 */

@AutoConfiguration
@ConditionalOnClass({CanalSyncRegistrar.class, RedissonClient.class})
@EnableConfigurationProperties(CanalClientProperties.class)
public class CanalRedisSyncAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public CanalSyncManager canalSyncManager() {
        return new CanalSyncManager();
    }

    @Bean
    @ConditionalOnMissingBean
    public EntityBuilder entityBuilder() {
        return new EntityBuilder();
    }

    @Bean
    @ConditionalOnMissingBean
    public CanalRedisSyncRunner canalRedisSyncRunner(CanalSyncManager syncManager, EntityBuilder entityBuilder, RedissonClient redissonClient) {
        return new CanalRedisSyncRunner(syncManager, entityBuilder, redissonClient);
    }

    @Bean
    @ConditionalOnMissingBean
    public CanalClientListener canalClientListener(CanalRedisSyncRunner syncRunner, CanalClientProperties properties) {
        return new CanalClientListener(syncRunner, properties);
    }

}
