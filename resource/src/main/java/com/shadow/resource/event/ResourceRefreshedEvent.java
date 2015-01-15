package com.shadow.resource.event;

import com.shadow.event.Event;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author nevermore on 2015/1/14
 */
public class ResourceRefreshedEvent implements Event {

    private final Class<?> resourceType;

    private ResourceRefreshedEvent(Class<?> resourceType) {
        this.resourceType = resourceType;
    }

    public static ResourceRefreshedEvent valueOf(@Nonnull Class<?> resourceType) {
        return new ResourceRefreshedEvent(Objects.requireNonNull(resourceType));
    }

    public Class<?> getResourceType() {
        return resourceType;
    }

    public boolean matchAny(Class<?>... types) {
        return types != null && Arrays.stream(types).anyMatch(type -> type == resourceType);
    }
}
