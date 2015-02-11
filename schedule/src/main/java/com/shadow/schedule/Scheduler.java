package com.shadow.schedule;

import org.springframework.scheduling.Trigger;

import java.util.Date;
import java.util.concurrent.ScheduledFuture;

/**
 * @author nevermore on 2015/2/11
 */
public interface Scheduler {

    ScheduledFuture<?> schedule(NamedTask task, String cron);

    ScheduledFuture<?> schedule(NamedTask task, Trigger trigger);

    ScheduledFuture<?> schedule(NamedTask task, Date startTime);

    ScheduledFuture<?> scheduleAtFixedRate(NamedTask task, Date startTime, long period);

    ScheduledFuture<?> scheduleAtFixedRate(NamedTask task, long period);

    ScheduledFuture<?> scheduleWithFixedDelay(NamedTask task, Date startTime, long delay);

    ScheduledFuture<?> scheduleWithFixedDelay(NamedTask task, long delay);
}
