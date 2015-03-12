package com.shadow.entity.cache;

import com.shadow.entity.IEntity;
import com.shadow.entity.cache.annotation.CacheIndex;
import com.shadow.entity.cache.annotation.Cacheable;
import com.shadow.util.injection.InjectedAnnotationProcessor;
import com.shadow.util.injection.ParameterizedTypeNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Set;

/**
 * @author nevermore on 2015/1/16.
 */
@SuppressWarnings("unchecked")
@Component
public class EntityCacheInjector implements InjectedAnnotationProcessor {

    @Autowired
    private EntityCacheManager entityCacheManager;

    @Override
    public void inject(Object target, Field field) throws IllegalAccessException {
        Type type = field.getGenericType();
        if (!(type instanceof ParameterizedType)) {
            throw new ParameterizedTypeNotFoundException(field);
        }
        ParameterizedType parameterizedType = (ParameterizedType) type;
        Type[] types = parameterizedType.getActualTypeArguments();
        Class<? extends IEntity<?>> entityClass = (Class<? extends IEntity<?>>) types[1];
        if (!entityClass.isAnnotationPresent(Cacheable.class)) {
            throw new IllegalStateException("实体类[" + entityClass.getName() + "]找不到" + Cacheable.class.getSimpleName() + "注解");
        }

        validate(field.getType(), entityClass);

        Object value = entityCacheManager.getEntityCache(entityClass);
        ReflectionUtils.makeAccessible(field);
        field.set(target, value);
    }

    @Override
    public Class<?> fieldType() {
        return EntityCache.class;
    }

    private void validate(Class<?> fieldType, Class<?> entityClass) {
        Set<Field> indexFields = org.reflections.ReflectionUtils.getAllFields(entityClass, type -> type.isAnnotationPresent(CacheIndex.class));
        if (indexFields.size() > 1) {
            throw new IllegalStateException(entityClass.getName() + "存在重复的@" + CacheIndex.class.getSimpleName() + "属性" + indexFields);
        }
        if (indexFields.isEmpty() && RegionEntityCache.class.isAssignableFrom(fieldType)) {
            throw new IllegalStateException(entityClass.getName() + "找不到@" + CacheIndex.class.getSimpleName() + "属性" + "无法使用" + RegionEntityCache.class.getSimpleName() + "缓存");
        }
    }
}
