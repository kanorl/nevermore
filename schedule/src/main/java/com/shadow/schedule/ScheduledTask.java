package com.shadow.schedule;

/**
 * 定时任务
 *
 * @author nevermore on 2015/4/9
 */
public interface ScheduledTask extends Runnable {
    String getName();
}
