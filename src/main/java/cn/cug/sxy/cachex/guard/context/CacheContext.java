package cn.cug.sxy.cachex.guard.context;

import lombok.Builder;
import lombok.Data;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @version 1.0
 * @Date 2025/5/18 15:17
 * @Description 统一策略上下文
 * @Author jerryhotton
 */

@Data
@Builder
public class CacheContext<T> {

    private T value;

    private String cacheKey;

    private Supplier<T> dbQuery;

    private Runnable dbUpdate;

    private Long ttl;

    private TimeUnit timeUnit;

}
