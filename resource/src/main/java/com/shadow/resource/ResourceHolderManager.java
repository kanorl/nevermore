package com.shadow.resource;

import com.shadow.event.EventBus;
import com.shadow.resource.event.ResourceRefreshedEvent;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author nevermore on 2015/1/14
 */
public class ResourceHolderManager {

    @Autowired
    private EventBus eventBus;

    public <V> ResourceHolder<V> getResourceHolder(Class<V> clazz) {
        return null;
    }

    public void reload(Class<?> clazz) {
        ResourceHolder<?> resourceHolder = getResourceHolder(clazz);
        if (resourceHolder == null) {
            throw new IllegalStateException();
        }
        resourceHolder.reload();

        eventBus.post(ResourceRefreshedEvent.valueOf(clazz));
    }
}
