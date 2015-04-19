package com.shadow.resource;

import com.shadow.common.util.lang.ReflectUtil;
import com.shadow.resource.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author nevermore on 2015/1/14
 */
@Component
public class ResourceHolderManager{

    @Autowired
    private ApplicationContext applicationContext;
    private Map<Class<?>, ResourceHolder<?>> holders = new HashMap<>();

    @PostConstruct
    private void init() {
        Set<Class<?>> resourceTypes = ReflectUtil.getTypesAnnotatedWith(Resource.class);
        resourceTypes.forEach(resourceType -> {
            ResourceHolder<?> resourceHolder = applicationContext.getAutowireCapableBeanFactory().createBean(ResourceHolder.class);
            resourceHolder.initialize(resourceType);
            holders.putIfAbsent(resourceType, resourceHolder);
        });
    }

    public <V> ResourceHolder<?> getResourceHolder(Class<V> clazz) {
        ResourceHolder<?> resourceHolder = holders.get(clazz);
        if (resourceHolder == null) {
            throw new IllegalStateException("No ResourceHolder for class[" + clazz.getName() + "]");
        }
        return resourceHolder;
    }

    public void reload(@Nonnull Class<?>... classes) {
        Objects.requireNonNull(classes);
        for (Class<?> type : classes) {
            getResourceHolder(type).reload();
        }
    }
}
