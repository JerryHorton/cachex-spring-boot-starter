package cn.cug.sxy.shared.cache.bloomFilter;

import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;

/**
 * @version 1.0
 * @Date 2025/5/16 14:47
 * @Description Redis 布隆过滤器
 * @Author jerryhotton
 */

public class RedisBloomFilter<T> {

    private final RBloomFilter<T> bloomFilter;

    public RedisBloomFilter(RedissonClient redissonClient, String bloomName, long expectedInsertions, double falseProbability) {
        this.bloomFilter = redissonClient.getBloomFilter(bloomName);
        if (!bloomFilter.isExists()) {
            bloomFilter.tryInit(expectedInsertions, falseProbability);
        }
    }

    public boolean add(T value) {
        return bloomFilter.add(value);
    }

    public boolean contains(T value) {
        return bloomFilter.contains(value);
    }

    public void delete() {
        bloomFilter.delete();
    }

}
