package cn.cug.sxy.shared.cache.redis_canal.annotation;

import java.lang.annotation.*;

/**
 * @version 1.0
 * @Date 2025/5/16 19:04
 * @Description canal同步器注解
 * @Author jerryhotton
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CanalSync {

    String databasePattern();

    String tablePattern();

    String redisKeyPrefix();

    String[] idFields();

}
