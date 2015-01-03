package com.shadow.util.thread;

import javax.annotation.Nonnull;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自定义命名线程工厂
 *
 * @author nevermore on 2014/11/26
 */
public final class NamedThreadFactory implements ThreadFactory {

    private AtomicInteger threadCounter = new AtomicInteger();
    private ThreadGroup threadGroup;
    private String groupName;
    private boolean isDaemon;
    private int priority;

    public NamedThreadFactory(String groupName) {
        this(groupName, false);
    }

    public NamedThreadFactory(String groupName, boolean isDaemon) {
        this(groupName, isDaemon, Thread.currentThread().getPriority());
    }

    public NamedThreadFactory(String groupName, boolean isDaemon, int priority) {
        if (priority < Thread.MIN_PRIORITY || priority > Thread.MAX_PRIORITY) {
            throw new IllegalArgumentException();
        }
        this.threadGroup = new ThreadGroup(groupName);
        this.groupName = groupName;
        this.isDaemon = isDaemon;
        this.priority = priority;
    }

    @Override
    public Thread newThread(@Nonnull Runnable r) {
        Thread thread = new Thread(threadGroup, r, groupName + "线程-" + threadCounter.incrementAndGet());
        thread.setDaemon(isDaemon);
        thread.setPriority(priority);
        return thread;
    }
}
