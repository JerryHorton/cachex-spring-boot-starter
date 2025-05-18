package cn.cug.sxy.cachex.sync.annotation;

import cn.cug.sxy.cachex.sync.CanalRedisSyncAutoConfiguration;
import cn.cug.sxy.cachex.sync.starter.CanalSyncRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @version 1.0
 * @Date 2025/5/16 22:19
 * @Description 开启CanalRedis同步
 * @Author jerryhotton
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({CanalRedisSyncAutoConfiguration.class, CanalSyncRegistrar.class})
public @interface EnableCanalRedisSync {

    String[] basePackages() default {};

}
