package com.shadow.resource;

import com.shadow.common.injection.InjectedAnnotationProcessor;
import com.shadow.common.injection.ParameterizedTypeNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author nevermore on 2015/1/16.
 */
@Component
public class ResourceHolderInjector implements InjectedAnnotationProcessor {

    @Autowired
    private ResourceHolderManager resourceHolderManager;

    @Override
    public void inject(Object target, Field field) throws IllegalAccessException {
        Type type = field.getGenericType();
        if (!(type instanceof ParameterizedType)) {
            throw new ParameterizedTypeNotFoundException(field);
        }
        ParameterizedType parameterizedType = (ParameterizedType) type;
        Type[] types = parameterizedType.getActualTypeArguments();
        Class<?> entityClass = (Class<?>) types[0];
        ResourceHolder<?> resourceHolder = resourceHolderManager.getResourceHolder(entityClass);
        ReflectionUtils.makeAccessible(field);
        field.set(target, resourceHolder);
    }

    @Override
    public Class<?> fieldType() {
        return ResourceHolder.class;
    }
}
