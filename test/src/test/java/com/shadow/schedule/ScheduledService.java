package com.shadow.schedule;

import org.springframework.stereotype.Component;

/**
 * @author nevermore on 2015/4/9
 */
@Component
public class ScheduledService {

    @Scheduled(name = "定时打印", value = "*/10 * * * * *", valueType = Scheduled.ValueType.CRON)
    private void print() {
        System.out.println("定时任务测试.........................");
    }
}
