package com.shadow.util.disruptor;

import com.lmax.disruptor.WorkHandler;
import com.shadow.util.lang.MathUtil;
import com.shadow.util.thread.NamedThreadFactory;

import java.util.concurrent.ThreadFactory;

/**
 * Disruptor构造器
 *
 * @author nevermore on 2014/11/26
 */
public final class DisruptorBuilder<T> {

    private int bufferSize;
    private int threadCount;
    private ThreadFactory threadFactory;

    public static final int DEFAULT_BUFFER_SIZE = 1 << 10;
    private static final int MIN_THREAD_COUNT = 1;
    private static final ThreadFactory DEFAULT_THREAD_FACTORY = new NamedThreadFactory("Disruptor");


    public static <T> DisruptorBuilder<T> newBuilder() {
        return new DisruptorBuilder<>();
    }

    public <T1 extends T> DisruptorService<T1> build(WorkHandler<Event<T1>> handler) {
        return new UnorderedDisruptor<>(this, handler);
    }

    public DisruptorBuilder<T> bufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
        return this;
    }

    public DisruptorBuilder<T> threadCount(int threadCount) {
        this.threadCount = threadCount;
        return this;
    }

    public DisruptorBuilder<T> threadFactory(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
        return this;
    }

    // -------------------Getter-------------------

    int getBufferSize() {
        return bufferSize <= 0 ? DEFAULT_BUFFER_SIZE : MathUtil.ensurePowerOf2(bufferSize);
    }

    int getThreadCount() {
        return Math.max(threadCount, MIN_THREAD_COUNT);
    }

    ThreadFactory getThreadFactory() {
        return threadFactory == null ? DEFAULT_THREAD_FACTORY : threadFactory;
    }
}
