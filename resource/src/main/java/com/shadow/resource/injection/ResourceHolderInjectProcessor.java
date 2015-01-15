package com.shadow.resource.injection;

import com.shadow.resource.ResourceHolder;
import com.shadow.resource.ResourceHolderManager;
import com.shadow.resource.annotation.InjectedResource;
import com.shadow.resource.annotation.Resource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author nevermore on 2015/1/14
 */
@Component
public class ResourceHolderInjectProcessor extends InstantiationAwareBeanPostProcessorAdapter {

    @Autowired
    private ResourceHolderManager resourceHolderManager;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        ReflectionUtils.doWithFields(bean.getClass(), field -> {
            if (!ResourceHolder.class.isAssignableFrom(field.getType())) {
                throw new UnsupportedOperationException();
            }
            Type type = field.getGenericType();
            if (!(type instanceof ParameterizedType)) {
                throw new RuntimeException();
            }
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] types = parameterizedType.getActualTypeArguments();
            Class<?> entityClass = (Class<?>) types[0];
            ResourceHolder<?> resourceHolder = resourceHolderManager.getResourceHolder(entityClass);
            Object value = resourceHolder;
            if (field.getType().isAnnotationPresent(Resource.class)) {
                value = resourceHolder.get(field.getAnnotation(InjectedResource.class).value());
            }
            ReflectionUtils.makeAccessible(field);
            field.set(bean, value);
        }, field -> field.isAnnotationPresent(InjectedResource.class));
        return super.postProcessBeforeInitialization(bean, beanName);
    }
}
