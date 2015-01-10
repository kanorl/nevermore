package com.shadow.util.disruptor;

import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.shadow.util.concurrent.ExecutorUtil;
import com.shadow.util.thread.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author nevermore on 2014/11/26
 */
final class UnorderedDisruptor<T> implements DisruptorService<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(UnorderedDisruptor.class);

    private final String name;
    private final Disruptor<Event<T>> disruptor;
    private final ExecutorService executorService;
    private final RingBuffer<Event<T>> ringBuffer;

    @SuppressWarnings("unchecked")
    public UnorderedDisruptor(String name, DisruptorBuilder<? super T> builder, WorkHandler<Event<T>> handler) {
        this.name = name;
        executorService = Executors.newFixedThreadPool(builder.getThreads(), new NamedThreadFactory(name));
        disruptor = new Disruptor<>(Event::new, builder.getBufferSize(), executorService);

        WorkHandler[] handlers = new WorkHandler[builder.getThreads()];
        Arrays.fill(handlers, handler);

        disruptor.handleExceptionsWith(new ExceptionHandler() {
            @Override
            public void handleEventException(Throwable ex, long sequence, Object event) {
                LOGGER.error("Disruptor事件处理异常: Event=" + event, ex);
            }

            @Override
            public void handleOnStartException(Throwable ex) {
                LOGGER.error("Disruptor启动异常", ex);
            }

            @Override
            public void handleOnShutdownException(Throwable ex) {
                LOGGER.error("Disruptor关闭异常", ex);
            }
        });
        disruptor.handleEventsWithWorkerPool(handlers);
        ringBuffer = disruptor.start();
    }

    @Override
    public void submit(T data) {
        RingBuffer<Event<T>> ringBuffer = disruptor.getRingBuffer();
        ringBuffer.publishEvent((event, sequence, arg0) -> event.setData(data), data);
    }

    @Override
    public void shutdown() {
        disruptor.shutdown();
        ExecutorUtil.shutdownAndAwaitTermination(executorService, name);
    }

    @Override
    public long remainEventCount() {
        return ringBuffer.getBufferSize() - ringBuffer.remainingCapacity();
    }
}
