package com.shadow.entity;

import com.shadow.test.module.player.model.Country;
import com.shadow.test.module.player.model.Gender;
import com.shadow.test.module.player.service.PlayerService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author nevermore on 2014/11/27
 */
public class EntityServiceTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(EntityServiceTest.class);

    private ClassPathXmlApplicationContext context;

    private long start;

    @Before
    public void before() {
        context = new ClassPathXmlApplicationContext("/applicationContext.xml");
        context.registerShutdownHook();
    }

    @Test
    public void test() throws InterruptedException {
        UserService userService = context.getBean(UserService.class);
        int n = 5000;
        start = System.currentTimeMillis();
        for (int i = 0; i < n; i++) {
            userService.addUser(i);
        }

        for (int i = 0; i < n; i++) {
            userService.updateUsername(i, "username" + 1);
        }

        PlayerService playerService = context.getBean(PlayerService.class);
        for (int i = 0; i < n; i++) {
            playerService.create((long) i, String.valueOf(i), Gender.FEMALE, Country.FOUR);
        }
    }

    @After
    public void after() throws InterruptedException {
        context.close();
        while (context.isRunning()) {
            Thread.yield();
        }

        LOGGER.error("耗时: " + (System.currentTimeMillis() - start) + "ms");
    }
}
