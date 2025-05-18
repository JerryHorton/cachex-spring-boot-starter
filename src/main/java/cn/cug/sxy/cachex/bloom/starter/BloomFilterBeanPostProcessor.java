package cn.cug.sxy.cachex.bloom.starter;

import cn.cug.sxy.cachex.bloom.annotation.BloomFilter;
import cn.cug.sxy.cachex.bloom.filter.RedisBloomFilter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;

/**
 * @version 1.0
 * @Date 2025/5/16 15:03
 * @Description 布隆过滤器Bean后置处理器
 * @Author jerryhotton
 */

public class BloomFilterBeanPostProcessor implements BeanPostProcessor {

    private final BloomFilterRegistry registry;

    public BloomFilterBeanPostProcessor(BloomFilterRegistry registry) {
        this.registry = registry;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            BloomFilter annotation = field.getAnnotation(BloomFilter.class);
            if (null != annotation) {
                String filterName = annotation.value();
                RedisBloomFilter<?> bloomFilter = registry.getBloomFilter(filterName);
                if (null != bloomFilter) {
                    field.setAccessible(true);
                    try {
                        field.set(bean, bloomFilter);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("注入布隆过滤器失败：" + filterName, e);
                    }
                } else {
                    throw new RuntimeException("布隆过滤器不存在：" + filterName);
                }
            }
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object o, String s) throws BeansException {
        return o;
    }

}
