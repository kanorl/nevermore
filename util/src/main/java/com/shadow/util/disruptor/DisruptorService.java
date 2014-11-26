package com.shadow.util.disruptor;

/**
 * @author nevermore on 2014/11/26
 */
public interface DisruptorService<T> {

    void submit(T data);

    void shutdown();

    long remainEventCount();
}
