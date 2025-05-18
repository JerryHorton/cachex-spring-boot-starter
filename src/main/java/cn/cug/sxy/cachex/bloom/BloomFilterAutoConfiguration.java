package cn.cug.sxy.shared.cache.bloomFilter;

import org.redisson.api.RedissonClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @version 1.0
 * @Date 2025/5/16 14:42
 * @Description 布隆过滤器自动配置器
 * @Author jerryhotton
 */

@Configuration
@EnableConfigurationProperties(BloomFilterProperties.class)
public class BloomFilterAutoConfiguration {

    @Bean
    public BloomFilterRegistry bloomFilterRegistry(
            RedissonClient redissonClient,
            BloomFilterProperties bloomFilterProperties) {
        return new BloomFilterRegistry(redissonClient, bloomFilterProperties);
    }

    @Bean
    public BloomFilterBeanPostProcessor bloomFilterBeanPostProcessor(BloomFilterRegistry registry) {
        return new BloomFilterBeanPostProcessor(registry);
    }

}
