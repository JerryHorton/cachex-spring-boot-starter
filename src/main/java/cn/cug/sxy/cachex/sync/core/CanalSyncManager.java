package cn.cug.sxy.shared.cache.redis_canal.core;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @version 1.0
 * @Date 2025/5/16 20:02
 * @Description 同步规则管理器
 * @Author jerryhotton
 */

@Component
public class CanalSyncManager {

    private final List<TableSyncRule> rules = new CopyOnWriteArrayList<>();

    public void registerRule(TableSyncRule rule) {
        rules.add(rule);
    }

    public List<TableSyncRule> getAllRules() {
        return rules;
    }

}
