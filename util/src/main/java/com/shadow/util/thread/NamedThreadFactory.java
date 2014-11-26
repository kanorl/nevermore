package com.shadow.util.thread;

import javax.annotation.Nonnull;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Objects.requireNonNull;

/**
 * 自定义命名线程工厂
 *
 * @author nevermore on 2014/11/26
 */
public final class NamedThreadFactory implements ThreadFactory {

    private AtomicInteger threadCounter = new AtomicInteger();
    private ThreadGroup threadGroup;
    private String threadName;

    public NamedThreadFactory(@Nonnull ThreadGroup threadGroup, String threadName) {
        this.threadGroup = requireNonNull(threadGroup);
        this.threadName = threadName;
    }


    @Override
    public Thread newThread(@Nonnull Runnable r) {
        Thread thread = new Thread(threadGroup, r, threadName + "-" + threadCounter.incrementAndGet());
        thread.setDaemon(true);
        return thread;
    }
}
