package cn.cug.sxy.shared.cache.redis_canal;

import cn.cug.sxy.shared.cache.redis_canal.canal.CanalRedisSyncRunner;
import cn.cug.sxy.shared.cache.redis_canal.config.CanalClientProperties;
import cn.cug.sxy.shared.cache.redis_canal.core.CanalSyncManager;
import cn.cug.sxy.shared.cache.redis_canal.core.EntityBuilder;
import cn.cug.sxy.shared.cache.redis_canal.listener.CanalClientListener;
import cn.cug.sxy.shared.cache.redis_canal.starter.CanalSyncRegistrar;
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
@ConditionalOnClass(CanalSyncRegistrar.class)
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
    public CanalRedisSyncRunner canalRedisSyncRunner() {
        return new CanalRedisSyncRunner();
    }

    @Bean
    @ConditionalOnMissingBean
    public CanalClientListener canalClientListener(CanalRedisSyncRunner syncRunner, CanalClientProperties properties) {
        return new CanalClientListener(syncRunner, properties);
    }

}
