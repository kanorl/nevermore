package com.shadow.resource;

import com.shadow.event.EventListener;
import com.shadow.resource.event.ResourceRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * @author nevermore on 2015/1/17
 */
@Component
public class TestResourceListener implements EventListener<ResourceRefreshedEvent> {
    @Override
    public void onEvent(ResourceRefreshedEvent event) {
        System.out.println(event.getResourceType().getSimpleName() + " Refreshed.");
    }
}
