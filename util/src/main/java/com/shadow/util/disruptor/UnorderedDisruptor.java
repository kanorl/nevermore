package com.shadow.util.disruptor;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.dsl.Disruptor;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author nevermore on 2014/11/26
 */
final class UnorderedDisruptor<T> implements DisruptorService<T> {

    private final Disruptor<Event<T>> disruptor;
    private final ExecutorService executorService;
    private final RingBuffer<Event<T>> ringBuffer;

    @SuppressWarnings("unchecked")
    public UnorderedDisruptor(DisruptorBuilder<? super T> builder, WorkHandler<Event<T>> handler) {
        executorService = Executors.newFixedThreadPool(builder.getThreads(), builder.getThreadFactory());
        disruptor = new Disruptor<>(Event::new, builder.getBufferSize(), executorService);

        WorkHandler[] handlers = new WorkHandler[builder.getThreads()];
        Arrays.fill(handlers, handler);

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
        executorService.shutdown();
    }

    @Override
    public long remainEventCount() {
        return ringBuffer.getBufferSize() - ringBuffer.remainingCapacity();
    }
}
