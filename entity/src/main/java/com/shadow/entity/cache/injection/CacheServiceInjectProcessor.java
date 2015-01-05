package com.shadow.entity.cache.injection;

import com.shadow.entity.IEntity;
import com.shadow.entity.annotation.RegionIndex;
import com.shadow.entity.cache.EntityCache;
import com.shadow.entity.cache.EntityCacheManager;
import com.shadow.entity.cache.RegionEntityCache;
import com.shadow.entity.cache.annotation.Cacheable;
import com.shadow.entity.cache.annotation.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

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
            if (!EntityCache.class.isAssignableFrom(field.getType()) && !RegionEntityCache.class.isAssignableFrom(field.getType())) {
                return;
            }
            Type type = field.getGenericType();
            if (!(type instanceof ParameterizedType)) {
                throw new RuntimeException();
            }
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] types = parameterizedType.getActualTypeArguments();
            Class<V> entityClass = (Class<V>) types[1];
            if (!entityClass.isAnnotationPresent(Cacheable.class)) {
                throw new RuntimeException();
            }

            validate(field.getType(), entityClass);

            Object value = EntityCache.class.isAssignableFrom(field.getType()) ? entityCacheManager.getEntityCache(entityClass) : entityCacheManager.getRegionEntityCache(entityClass);
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
        if ((RegionEntityCache.class.isAssignableFrom(fieldType) && org.reflections.ReflectionUtils.getAllFields(entityClass, type -> type.isAnnotationPresent(RegionIndex.class)).isEmpty())
                || (EntityCache.class.isAssignableFrom(fieldType) && !org.reflections.ReflectionUtils.getAllFields(entityClass, type -> type.isAnnotationPresent(RegionIndex.class)).isEmpty())) {
            throw new UnsupportedOperationException();
        }
    }
}
