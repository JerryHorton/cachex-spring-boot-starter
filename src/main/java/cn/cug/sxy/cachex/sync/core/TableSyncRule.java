package cn.cug.sxy.shared.cache.redis_canal.core;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @version 1.0
 * @Date 2025/5/16 19:10
 * @Description 规则实体
 * @Author jerryhotton
 */

public class TableSyncRule {

    private final Pattern databasePattern;

    private final Pattern tablePattern;

    private final String redisKeyPrefix;

    private final List<String> idField;

    private final Class<?> clazz;

    public TableSyncRule(Pattern databasePattern, Pattern tablePattern, String redisKeyPrefix, String[] idField, Class<?> clazz) {
        this.databasePattern = databasePattern;
        this.tablePattern = tablePattern;
        this.redisKeyPrefix = redisKeyPrefix;
        this.idField = Arrays.asList(idField);
        this.clazz = clazz;
    }

    public boolean match(String database, String table) {
        return databasePattern.matcher(database).matches() && tablePattern.matcher(table).matches();
    }

    public String getRedisKeyPrefix() {
        return redisKeyPrefix;
    }

    public List<String> getIdField() {
        return idField;
    }

    public Class<?> getClazz() {
        return clazz;
    }

}
