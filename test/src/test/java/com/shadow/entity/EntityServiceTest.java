package com.shadow.entity;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author nevermore on 2014/11/27
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/applicationContext.xml")
public class EntityServiceTest {

    @Autowired
    private UserService userService;

    private long start;

    @Test
    public void test() {
        start = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            userService.addUser(i);
        }
    }

    @After
    public void after() throws InterruptedException {
        System.out.println("耗时: " + (System.currentTimeMillis() - start) + "ms");
//        TimeUnit.SECONDS.sleep(10);
    }
}
