package cn.cug.sxy.cachex.sync.core;

import cn.cug.sxy.cachex.sync.util.ColumnFieldConverter;
import com.alibaba.otter.canal.protocol.CanalEntry.Column;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @version 1.0
 * @Date 2025/5/16 19:15
 * @Description 实体构建器
 * @Author jerryhotton
 */

@Slf4j
public class EntityBuilder {

    public <T> T build(List<Column> columns, Class<T> clazz) {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();
            for (Column column : columns) {
                String fieldName = ColumnFieldConverter.underlineToCamel(column.getName());
                String value = column.getValue();
                try {
                    Field field = clazz.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    Object realValue = convert(field.getType(), value);
                    field.set(instance, realValue);
                    field.setAccessible(false);
                } catch (NoSuchFieldException ignored) {
                    log.warn("忽略不匹配字段 {}", fieldName);
                }
            }
            return instance;
        } catch (Exception e) {
            log.error("构建实体失败", e);
            return null;
        }
    }

    private Object convert(Class<?> type, String value) throws ParseException {
        if (value == null) return null;
        if (type == Long.class || type == long.class) return Long.valueOf(value);
        if (type == Integer.class || type == int.class) return Integer.valueOf(value);
        if (type == BigDecimal.class) return new BigDecimal(value);
        if (type == Boolean.class || type == boolean.class) return Boolean.valueOf(value);
        if (type == Date.class) return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(value);
        return value;
    }

}
