package com.shadow.event;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.TimeUnit;

/**
 * @author nevermore on 2014/12/28.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/applicationContext.xml")
public class EventTest {

    @Autowired
    private EventBus eventBus;

    @Test
    public void test() throws InterruptedException {
        eventBus.post(new MyEvent("a"));
        eventBus.post(new MyEvent("b"));
        eventBus.post(new MyEvent("c"));
        eventBus.post(new MyEvent("d"));
        eventBus.post(new MyEvent2(2));
        eventBus.post(new Event() {
        });
        TimeUnit.SECONDS.sleep(3);
    }
}
