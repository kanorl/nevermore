package com.shadow.entity;

import com.shadow.entity.cache.EntityCacheServiceManager;
import com.shadow.entity.orm.persistence.PersistenceProcessor;
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

    @Autowired
    private EntityCacheServiceManager<Integer, User> serviceManager;


    @Test
    public void test() {
        PersistenceProcessor<User> persistenceProcessor = serviceManager.getPersistenceProcessor(User.class);
        Long start = System.currentTimeMillis();
        for (int i = 0; i < 5000; i++) {
            userService.addUser(i);
        }

        while (persistenceProcessor.remainTasks() > 0) {

        }
        System.out.println("耗时: " + (System.currentTimeMillis() - start) + "ms");
    }

    @After
    public void after() throws InterruptedException {
        TimeUnit.SECONDS.sleep(60);
    }
}
