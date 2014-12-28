package com.shadow.event;

import org.springframework.stereotype.Component;

/**
 * @author nevermore on 2014/12/28.
 */
@Component
public class TestEventListener1 implements EventListener<MyEvent> {
    @Override
    public void onEvent(MyEvent event) {
        System.out.println(Thread.currentThread().getName() + ":" + event.getName());
    }
}
