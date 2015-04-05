package com.shadow.common.util;

import com.shadow.entity.UserService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author nevermore on 2014/12/5.
 */
public class DisruptorTest {

    public static void main(String[] args) throws InterruptedException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("/applicationContext.xml");
        context.registerShutdownHook();
        context.start();

        UserService userService = context.getBean("userService", UserService.class);
        for (int i = 0; i < 100; i++) {
            userService.addUser(i);
        }

        context.close();
    }
}
