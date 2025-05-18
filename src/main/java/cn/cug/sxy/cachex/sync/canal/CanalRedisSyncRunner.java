package cn.cug.sxy.cachex.sync.canal;

import cn.cug.sxy.cachex.sync.core.CanalSyncManager;
import cn.cug.sxy.cachex.sync.core.EntityBuilder;
import cn.cug.sxy.cachex.sync.core.TableSyncRule;
import cn.cug.sxy.cachex.sync.util.ColumnFieldConverter;
import com.alibaba.otter.canal.protocol.CanalEntry.Column;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RedissonClient;

import java.util.ArrayList;
import java.util.List;

/**
 * @version 1.0
 * @Date 2025/5/16 20:07
 * @Description Canal监听核心
 * @Author jerryhotton
 */

@Slf4j
@RequiredArgsConstructor
public class CanalRedisSyncRunner {

    private final CanalSyncManager syncManager;

    private final EntityBuilder entityBuilder;

    private final RedissonClient redissonClient;

    public void onRowChange(String dataBase, String table, List<Column> columns) {
        for (TableSyncRule rule : syncManager.getAllRules()) {
            if (rule.match(dataBase, table)) {
                Object entity = entityBuilder.build(columns, rule.getClazz());
                if (null == entity) {
                    log.warn("实体构建失败，跳过同步");
                    continue;
                }
                String idValue = getIdValue(columns, rule.getIdField());
                if (StringUtils.isBlank(idValue)) {
                    log.warn("未找到主键值，跳过同步");
                    continue;
                }
                String cacheKey = rule.getRedisKeyPrefix() + idValue;
                redissonClient.getBucket(cacheKey).set(entity);
                log.info("同步完成，cacheKey: {}", cacheKey);
            }
        }
    }

    public void onRowDelete(String dataBase, String table, List<Column> columns) {
        for (TableSyncRule rule : syncManager.getAllRules()) {
            if (rule.match(dataBase, table)) {
                String idValue = getIdValue(columns, rule.getIdField());
                if (StringUtils.isBlank(idValue)) {
                    log.warn("未找到主键值，跳过同步");
                    continue;
                }
                String cacheKey = rule.getRedisKeyPrefix() + idValue;
                redissonClient.getBucket(cacheKey).delete();
                log.info("同步完成，cacheKey: {}", cacheKey);
            }
        }
    }

    private String getIdValue(List<Column> columns, List<String> idFields) {
        List<String> idValues = new ArrayList<>();
        for (String idField : idFields) {
            columns.stream()
                    .filter(column -> ColumnFieldConverter.underlineToCamel(column.getName()).equalsIgnoreCase(idField))
                    .map(Column::getValue)
                    .findFirst().ifPresent(idValues::add);
        }
        return String.join("_", idValues);
    }

}
