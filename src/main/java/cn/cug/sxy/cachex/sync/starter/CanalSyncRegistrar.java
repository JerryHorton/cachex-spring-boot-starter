package cn.cug.sxy.shared.cache.redis_canal.starter;

import cn.cug.sxy.shared.cache.redis_canal.annotation.CanalSync;
import cn.cug.sxy.shared.cache.redis_canal.annotation.EnableCanalRedisSync;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @version 1.0
 * @Date 2025/5/16 20:43
 * @Description 自动注册器
 * @Author jerryhotton
 */

@Slf4j
public class CanalSyncRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {

    private  ResourceLoader resourceLoader;

    public static final List<Class<?>> TABLE_SYNC_RULE_CLASSES = new ArrayList<>();

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        // 1. 获取 @EnableCanalRedisSync 配置的 basePackages
        Map<String, Object> attributes = importingClassMetadata.getAnnotationAttributes(EnableCanalRedisSync.class.getName());
        String[] basePackages = (String[]) attributes.get("basePackages");
        if (null == basePackages || basePackages.length == 0) {
            // 若未配置，则使用启动类所在的包
            String className = importingClassMetadata.getClassName();
            try {
                Class<?> clazz = Class.forName(className);
                String packageName = clazz.getPackage().getName();
                basePackages = new String[]{packageName};
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("无法确定默认扫描路径", e);
            }
        }
        // 2. 扫描带有 @CanalSync 注解的类
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(CanalSync.class));
        scanner.setResourceLoader(resourceLoader);
        for (String basePackage : basePackages) {
            for (BeanDefinition beanDefinition : scanner.findCandidateComponents(basePackage)) {
                try {
                    Class<?> clazz = Class.forName(beanDefinition.getBeanClassName());
                    CanalSync annotation = clazz.getAnnotation(CanalSync.class);
                    if (null == annotation) {
                        continue;
                    }
                    TABLE_SYNC_RULE_CLASSES.add(clazz);
                    log.info("注册CanalSync规则: {}", clazz.getName());
                } catch (Exception e) {
                    log.error("注册CanalSync规则失败", e);
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

}
