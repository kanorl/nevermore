package com.shadow.event;

import com.shadow.util.codec.JsonUtil;
import org.springframework.stereotype.Component;

/**
 * @author nevermore on 2014/12/28.
 */
@Component
public class TestEventListener2 implements EventListener<Event> {
    @Override
    public void onEvent(Event event) {
        if (event instanceof MyEvent) {
            System.out.println(((MyEvent) event).getName() + ":" + Thread.currentThread().getName());
        } else {
            System.out.println(JsonUtil.toJson(event));
        }
    }
}
