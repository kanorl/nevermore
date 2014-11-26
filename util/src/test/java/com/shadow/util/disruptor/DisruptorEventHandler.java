package com.shadow.util.disruptor;

import com.lmax.disruptor.EventHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author nevermore on 2014/11/26
 */
public class DisruptorEventHandler implements EventHandler<Event<Integer>> {
    @Override
    public void onEvent(Event<Integer> event, long sequence, boolean endOfBatch) throws Exception {
        TimeUnit.SECONDS.sleep(2);
        System.out.println(event.getData());
    }
}
