package cn.cug.sxy.cachex.guard.core;

import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @version 1.0
 * @Date 2025/5/18 20:39
 * @Description redis 读写一致性
 * @Author jerryhotton
 */

public class RedisConsistency {

    private final RedissonClient redissonClient;

    public RedisConsistency(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    public <T> T read(String cacheKey, String lockKey, Supplier<T> dbQuery, boolean strongConsistency) {
        if (strongConsistency) {
            return readWithStrongConsistency(cacheKey, lockKey, dbQuery);
        } else {
            return readWithEventualConsistency(cacheKey, lockKey, dbQuery);
        }
    }

    public <T> void write(String cacheKey, String lockKey, Supplier<T> dbQuery, Runnable dbUpdate) {
        writeWithStrongConsistency(cacheKey, lockKey, dbQuery, dbUpdate);
    }

    private <T> T readWithEventualConsistency(String cacheKey, String lockKey, Supplier<T> dbQuery) {
        T value = redissonClient.<T>getBucket(cacheKey).get();
        if (null != value) {
            return value;
        }
        return readWithStrongConsistency(cacheKey, lockKey, dbQuery);
    }

    private <T> T readWithStrongConsistency(String cacheKey, String lockKey, Supplier<T> dbQuery) {
        RReadWriteLock rwLock = redissonClient.getReadWriteLock(lockKey);
        RLock readLock = rwLock.readLock();
        try {
            // 防止死锁
            readLock.lock(3, TimeUnit.SECONDS);
            // 检查缓存
            T cacheValue = redissonClient.<T>getBucket(cacheKey).get();
            if (null != cacheValue) {
                return cacheValue;
            }
            // 查询数据库
            T dbValue = dbQuery.get();
            // 写入缓存
            if (null != dbValue) {
                redissonClient.<T>getBucket(cacheKey).set(dbValue);
            }
            return dbValue;
        } finally {
            if (readLock.isLocked() && readLock.isHeldByCurrentThread()) {
                readLock.unlock();
            }
        }
    }

    private <T> void writeWithStrongConsistency(String cacheKey, String lockKey, Supplier<T> dbQuery, Runnable dbUpdate) {
        RReadWriteLock rwLock = redissonClient.getReadWriteLock(lockKey);
        RLock writeLock = rwLock.writeLock();
        try {
            writeLock.lock(3, TimeUnit.SECONDS);
            // 更新数据库
            dbUpdate.run();
            // 更新缓存
            T newValue = dbQuery.get();
            if (null != newValue) {
                redissonClient.<T>getBucket(cacheKey).set(newValue);
            } else {
                redissonClient.getBucket(cacheKey).delete();
            }
        } finally {
            if (writeLock.isLocked() && writeLock.isHeldByCurrentThread()) {
                writeLock.unlock();
            }
        }
    }

}
