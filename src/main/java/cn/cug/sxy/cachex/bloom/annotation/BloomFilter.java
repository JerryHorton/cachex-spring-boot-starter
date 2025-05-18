package cn.cug.sxy.cachex.bloom.annotation;

import java.lang.annotation.*;

/**
 * @version 1.0
 * @Date 2025/5/16 14:24
 * @Description 布隆过滤器
 * @Author jerryhotton
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BloomFilter {
    String value();
}
