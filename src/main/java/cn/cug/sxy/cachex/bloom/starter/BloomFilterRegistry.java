package cn.cug.sxy.cachex.bloom.starter;

import cn.cug.sxy.cachex.bloom.config.BloomFilterProperties;
import cn.cug.sxy.cachex.bloom.filter.RedisBloomFilter;
import org.redisson.api.RedissonClient;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @version 1.0
 * @Date 2025/5/16 14:44
 * @Description 布隆过滤器注册器
 * @Author jerryhotton
 */

public class BloomFilterRegistry {

    private final Map<String, RedisBloomFilter<?>> bloomFilterMap = new ConcurrentHashMap<>();

    public BloomFilterRegistry(RedissonClient redissonClient, BloomFilterProperties properties) {
        for (Map.Entry<String, BloomFilterProperties.FilterConfig> entry : properties.getFilters().entrySet()) {
            String bloomName = entry.getKey();
            BloomFilterProperties.FilterConfig filterConfig = entry.getValue();
            RedisBloomFilter<Objects> bloomFilter = new RedisBloomFilter<>(
                    redissonClient, bloomName, filterConfig.getExpectedInsertions(), filterConfig.getFalseProbability());
            bloomFilterMap.put(bloomName, bloomFilter);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> RedisBloomFilter<T> getBloomFilter(String bloomName) {
        return (RedisBloomFilter<T>) bloomFilterMap.get(bloomName);
    }

}
