package cn.cug.sxy.cachex.sync.util;

/**
 * @version 1.0
 * @Date 2025/5/18 10:46
 * @Description 数据库列 与 Java 字段之间的转换器
 * @Author jerryhotton
 */

public class ColumnFieldConverter {

    public static String underlineToCamel(String name) {
        StringBuilder result = new StringBuilder();
        boolean upper = false;
        for (char c : name.toCharArray()) {
            if (c == '_') {
                upper = true;
            } else {
                result.append(upper ? Character.toUpperCase(c) : c);
                upper = false;
            }
        }
        return result.toString();
    }

}
