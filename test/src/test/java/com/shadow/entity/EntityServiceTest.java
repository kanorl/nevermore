package com.shadow.entity;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.TimeUnit;

/**
 * @author nevermore on 2014/11/27
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/applicationContext.xml")
public class EntityServiceTest {

    @Autowired
    private UserService userService;


    @Test
    public void test() {
        System.out.println(userService.getUser(1));
        System.out.println(userService.updateUsername(1, "1111"));
    }

    @After
    public void after() throws InterruptedException {
        TimeUnit.SECONDS.sleep(3);
    }
}
