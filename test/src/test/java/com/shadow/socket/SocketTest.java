package com.shadow.socket;

import org.junit.After;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.TimeUnit;

/**
 * @author nevermore on 2014/11/30.
 */
public class SocketTest {


    @Test
    public void test() throws InterruptedException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("/applicationContext.xml");
        context.start();

        while (context.isActive()) {
            TimeUnit.SECONDS.sleep(10000);
        }
    }

    @After
    public void after() {
    }
}
