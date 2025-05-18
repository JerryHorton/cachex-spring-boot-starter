package cn.cug.sxy.cachex.bloom.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * @version 1.0
 * @Date 2025/5/16 14:37
 * @Description 布隆过滤器配置
 * @Author jerryhotton
 */

@ConfigurationProperties(prefix = "cachex.redis.bloom")
public class BloomFilterProperties {

    private Map<String, FilterConfig> filters = new HashMap<>();

    public Map<String, FilterConfig> getFilters() {
        return filters;
    }

    public void setFilters(Map<String, FilterConfig> filters) {
        this.filters = filters;
    }

    public static class FilterConfig {

        private long expectedInsertions;

        private double falseProbability;

        public long getExpectedInsertions() {
            return expectedInsertions;
        }

        public void setExpectedInsertions(long expectedInsertions) {
            this.expectedInsertions = expectedInsertions;
        }

        public double getFalseProbability() {
            return falseProbability;
        }

        public void setFalseProbability(double falseProbability) {
            this.falseProbability = falseProbability;
        }

    }
}
