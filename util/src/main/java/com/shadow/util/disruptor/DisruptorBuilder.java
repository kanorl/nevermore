package com.shadow.util.disruptor;

import com.lmax.disruptor.WorkHandler;
import com.shadow.util.lang.MathUtil;

import java.util.concurrent.ThreadFactory;

/**
 * Disruptor构造器
 *
 * @author nevermore on 2014/11/26
 */
public final class DisruptorBuilder<T> {

    private int bufferSize;
    private int threads;
    private ThreadFactory threadFactory;

    public static final int DEFAULT_BUFFER_SIZE = 1 << 10;
    private static final int MIN_THREAD_COUNT = 1;

    public static <T> DisruptorBuilder<T> newBuilder() {
        return new DisruptorBuilder<>();
    }

    /**
     * 构造一个Disruptor服务
     *
     * @param name    名称
     * @param handler 事件处理器(必须为线程安全的)
     * @return
     */
    public <T1 extends T> DisruptorService<T1> build(String name, WorkHandler<Event<T1>> handler) {
        return new UnorderedDisruptor<>(name, this, handler);
    }

    /**
     * Disruptor的RingBuffer大小，当RingBuffer被占满时，提交任务需要等待，直到RingBuffer有空间
     *
     * @param bufferSize
     * @return
     */
    public DisruptorBuilder<T> bufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
        return this;
    }

    /**
     * 事件消费者线程数量
     *
     * @param threads
     * @return
     */
    public DisruptorBuilder<T> threads(int threads) {
        this.threads = threads;
        return this;
    }

    // -------------------Getter-------------------

    int getBufferSize() {
        return bufferSize <= 0 ? DEFAULT_BUFFER_SIZE : MathUtil.ensurePowerOf2(bufferSize);
    }

    int getThreads() {
        return Math.max(threads, MIN_THREAD_COUNT);
    }
}
