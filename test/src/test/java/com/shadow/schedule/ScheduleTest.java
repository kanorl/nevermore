package com.shadow.schedule;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author nevermore on 2015/2/11
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/applicationContext.xml")
@Component
public class ScheduleTest {

    @Autowired
    private Scheduler scheduler;

    @Test
    public void test() {

        Instant instant = LocalDateTime.parse("2015-04-09T11:26:10").atZone(ZoneId.systemDefault()).toInstant();

        scheduler.schedule(new ScheduledTask() {
            @Override
            public String getName() {
                return "定时调用测试";
            }

            @Override
            public void run() {
                System.out.println("=============定时调用测试============> " + new Date());
            }
        }, Date.from(instant));
    }

    @After
    public void tearDown() throws InterruptedException {
        TimeUnit.MINUTES.sleep(5);
    }

}
