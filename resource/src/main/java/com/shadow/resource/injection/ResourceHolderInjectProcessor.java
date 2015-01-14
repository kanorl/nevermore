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
            if (!ResourceHolder.class.isAssignableFrom(field.getType()) && !field.getType().isAnnotationPresent(Resource.class)) {
                throw new IllegalStateException();
            }
            ResourceHolder<?> resourceHolder = resourceHolderManager.getResourceHolder(bean.getClass());
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
