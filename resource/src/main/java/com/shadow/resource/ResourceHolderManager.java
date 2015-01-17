package com.shadow.resource;

import com.shadow.resource.annotation.Resource;
import com.shadow.util.lang.ReflectUtil;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author nevermore on 2015/1/14
 */
public class ResourceHolderManager implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    private Map<Class<?>, ResourceHolder<?>> holders = new HashMap<>();

    @PostConstruct
    private void init() {
        Set<Class<?>> resourceTypes = ReflectUtil.getTypesAnnotatedWith(Resource.class);
        resourceTypes.forEach(resourceType -> {
            ResourceHolder<?> resourceHolder = applicationContext.getAutowireCapableBeanFactory().createBean(ResourceHolder.class);
            holders.putIfAbsent(resourceType, resourceHolder);
        });
        holders.forEach((k, v) -> v.initialize(k));
    }

    public <V> ResourceHolder<?> getResourceHolder(Class<V> clazz) {
        ResourceHolder<?> resourceHolder = holders.get(clazz);
        if (resourceHolder == null) {
            throw new IllegalStateException("No ResourceHolder for class[" + clazz.getName() + "]");
        }
        return resourceHolder;
    }

    public void reload(Class<?> clazz) {
        getResourceHolder(clazz).reload();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
