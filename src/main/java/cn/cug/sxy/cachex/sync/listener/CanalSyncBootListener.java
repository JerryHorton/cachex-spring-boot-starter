package cn.cug.sxy.shared.cache.redis_canal.listener;

import cn.cug.sxy.shared.cache.redis_canal.annotation.CanalSync;
import cn.cug.sxy.shared.cache.redis_canal.core.CanalSyncManager;
import cn.cug.sxy.shared.cache.redis_canal.core.TableSyncRule;
import cn.cug.sxy.shared.cache.redis_canal.starter.CanalSyncRegistrar;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.regex.Pattern;

/**
 * @version 1.0
 * @Date 2025/5/17 11:21
 * @Description 启动监听器 延迟执行规则注册逻辑
 * @Author jerryhotton
 */

@Component
public class CanalSyncBootListener implements ApplicationListener<ContextRefreshedEvent> {

    @Resource
    private CanalSyncManager syncManager;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        for (Class<?> clazz : CanalSyncRegistrar.TABLE_SYNC_RULE_CLASSES) {
            CanalSync annotation = clazz.getAnnotation(CanalSync.class);
            if (null == annotation) {
                continue;
            }
            TableSyncRule rule = new TableSyncRule(
                    Pattern.compile(annotation.databasePattern()),
                    Pattern.compile(annotation.tablePattern()),
                    annotation.redisKeyPrefix(),
                    annotation.idFields(),
                    clazz
            );
            syncManager.registerRule(rule);
        }
    }

}
