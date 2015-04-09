package com.shadow.schedule;

import org.springframework.scheduling.Trigger;

import java.util.Date;
import java.util.concurrent.ScheduledFuture;

/**
 * @author nevermore on 2015/2/11
 */
public interface Scheduler {

    ScheduledFuture<?> schedule(ScheduledTask task, String cron);

    ScheduledFuture<?> schedule(ScheduledTask task, Trigger trigger);

    ScheduledFuture<?> schedule(ScheduledTask task, Date startTime);

    ScheduledFuture<?> scheduleAtFixedRate(ScheduledTask task, Date startTime, long period);

    ScheduledFuture<?> scheduleAtFixedRate(ScheduledTask task, long period);

    ScheduledFuture<?> scheduleWithFixedDelay(ScheduledTask task, Date startTime, long delay);

    ScheduledFuture<?> scheduleWithFixedDelay(ScheduledTask task, long delay);
}
