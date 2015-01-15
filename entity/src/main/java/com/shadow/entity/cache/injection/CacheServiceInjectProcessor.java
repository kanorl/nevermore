package com.shadow.entity.cache.injection;

import com.shadow.entity.IEntity;
import com.shadow.entity.annotation.CacheIndex;
import com.shadow.entity.annotation.Cacheable;
import com.shadow.entity.annotation.Inject;
import com.shadow.entity.cache.EntityCache;
import com.shadow.entity.cache.EntityCacheManager;
import com.shadow.entity.cache.RegionEntityCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Set;

/**
 * 缓存服务注入处理器
 *
 * @author nevermore on 2014/11/26.
 */
@SuppressWarnings("unchecked")
@Component
public class CacheServiceInjectProcessor<K extends Serializable, V extends IEntity<K>> extends InstantiationAwareBeanPostProcessorAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(CacheServiceInjectProcessor.class);

    @Autowired
    private EntityCacheManager<K, V> entityCacheManager;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        ReflectionUtils.doWithFields(bean.getClass(), field -> {
            if (!EntityCache.class.isAssignableFrom(field.getType())) {
                throw new RuntimeException();
            }
            Type type = field.getGenericType();
            if (!(type instanceof ParameterizedType)) {
                throw new RuntimeException();
            }
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] types = parameterizedType.getActualTypeArguments();
            Class<V> entityClass = (Class<V>) types[1];
            if (!entityClass.isAnnotationPresent(Cacheable.class)) {
                throw new IllegalStateException("实体类[" + entityClass.getName() + "]找不到" + Cacheable.class.getSimpleName() + "注解");
            }

            validate(field.getType(), entityClass);

            Object value = entityCacheManager.getEntityCache(entityClass);
            try {
                ReflectionUtils.makeAccessible(field);
                field.set(bean, value);
            } catch (IllegalAccessException e) {
                LOGGER.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }, field -> field.isAnnotationPresent(Inject.class));
        return super.postProcessBeforeInitialization(bean, beanName);
    }

    private void validate(Class<?> fieldType, Class<V> entityClass) {
        Set<Field> indexFields = org.reflections.ReflectionUtils.getAllFields(entityClass, type -> type.isAnnotationPresent(CacheIndex.class));
        if (indexFields.size() > 1) {
            throw new IllegalStateException(entityClass.getName() + "存在重复的@" + CacheIndex.class.getSimpleName() + "属性" + indexFields);
        }
        if (indexFields.isEmpty() && RegionEntityCache.class.isAssignableFrom(fieldType)) {
            throw new IllegalStateException(entityClass.getName() + "找不到@" + CacheIndex.class.getSimpleName() + "属性" + "无法使用" + RegionEntityCache.class.getSimpleName() + "缓存");
        }
    }
}
