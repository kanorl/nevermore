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
    private String threadName;
    private boolean isDaemon;
    private int priority;

    public NamedThreadFactory(String threadName) {
        this(threadName, false);
    }

    public NamedThreadFactory(String threadName, boolean isDaemon) {
        this(threadName, isDaemon, Thread.currentThread().getPriority());
    }

    public NamedThreadFactory(String threadName, boolean isDaemon, int priority) {
        if (priority < Thread.MIN_PRIORITY || priority > Thread.MAX_PRIORITY) {
            throw new IllegalArgumentException();
        }
        this.threadGroup = new ThreadGroup(threadName);
        this.threadName = threadName;
        this.isDaemon = isDaemon;
        this.priority = priority;
    }

    @Override
    public Thread newThread(@Nonnull Runnable r) {
        Thread thread = new Thread(threadGroup, r, threadName + "-" + threadCounter.incrementAndGet());
        thread.setDaemon(isDaemon);
        thread.setPriority(priority);
        return thread;
    }
}
