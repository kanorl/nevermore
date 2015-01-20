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

    private final AtomicInteger threadCounter = new AtomicInteger();
    private final String name;
    private final boolean isDaemon;
    private final int priority;

    public NamedThreadFactory(String name) {
        this(name, false);
    }

    public NamedThreadFactory(String name, boolean isDaemon) {
        this(name, isDaemon, Thread.currentThread().getPriority());
    }

    public NamedThreadFactory(String name, boolean isDaemon, int priority) {
        if (priority < Thread.MIN_PRIORITY || priority > Thread.MAX_PRIORITY) {
            throw new IllegalArgumentException();
        }
        this.name = name;
        this.isDaemon = isDaemon;
        this.priority = priority;
    }

    @Override
    public Thread newThread(@Nonnull Runnable r) {
        Thread thread = new Thread(r, name + "线程-" + threadCounter.incrementAndGet());
        thread.setDaemon(isDaemon);
        thread.setPriority(priority);
        return thread;
    }

    public String getName() {
        return name;
    }
}
