package com.shadow.schedule;

import com.shadow.schedule.executor.TimeChangeSensitiveScheduledThreadPoolExecutor;
import com.shadow.util.execution.LoggedExecution;
import com.shadow.util.thread.NamedThreadFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;

/**
 * @author nevermore on 2015/2/11
 */
@Component
public class DefaultScheduler extends ThreadPoolTaskScheduler implements Scheduler {

	private static final long serialVersionUID = 5403439503341559182L;
	
	@Value("${server.schedule.maxAwaitMills:5000}")
    private long maxAwaitMills;
    @Value("${server.schedule.poolSize:16}")
    private int poolSize;

    @Override
    protected ScheduledExecutorService createExecutor(int poolSize, ThreadFactory threadFactory, RejectedExecutionHandler rejectedExecutionHandler) {
        return new TimeChangeSensitiveScheduledThreadPoolExecutor(this.poolSize, maxAwaitMills, new NamedThreadFactory("定时任务处理"));
    }

    @Override
    public ScheduledFuture<?> schedule(NamedTask task, String cron) {
        return schedule(task, new CronTrigger(cron));
    }

    @Override
    public ScheduledFuture<?> schedule(NamedTask task, Trigger trigger) {
        return schedule(decorate(task), trigger);
    }

    @Override
    public ScheduledFuture<?> schedule(NamedTask task, Date startTime) {
        return schedule(decorate(task), startTime);
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(NamedTask task, Date startTime, long period) {
        return scheduleAtFixedRate(decorate(task), startTime, period);
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(NamedTask task, long period) {
        return scheduleAtFixedRate(decorate(task), period);
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(NamedTask task, Date startTime, long delay) {
        return scheduleWithFixedDelay(decorate(task), startTime, delay);
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(NamedTask task, long delay) {
        return scheduleWithFixedDelay(decorate(task), delay);
    }

    private Runnable decorate(NamedTask task) {
        return () -> LoggedExecution.forName(task.getName()).execute(task);
    }
}

