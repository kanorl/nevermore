package com.shadow.resource.event;

import com.shadow.event.Event;

/**
 * @author nevermore on 2015/1/14
 */
public class ResourceRefreshedEvent implements Event {

    private final Class<?> type;

    private ResourceRefreshedEvent(Class<?> type) {
        this.type = type;
    }

    public static ResourceRefreshedEvent valueOf(Class<?> type) {
        return new ResourceRefreshedEvent(type);
    }

    public Class<?> getType() {
        return type;
    }
}
