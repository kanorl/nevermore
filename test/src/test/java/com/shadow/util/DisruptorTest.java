package com.shadow.util;

import com.lmax.disruptor.WorkHandler;
import com.shadow.util.disruptor.DisruptorBuilder;
import com.shadow.util.disruptor.DisruptorService;
import com.shadow.util.disruptor.Event;

import java.util.concurrent.TimeUnit;

/**
 * @author nevermore on 2014/12/5.
 */
public class DisruptorTest {

    public static void main(String[] args) throws InterruptedException {
        DisruptorService<Integer> disruptorService = DisruptorBuilder.newBuilder().threads(3).build(new WorkHandler<Event<Integer>>() {
            @Override
            public void onEvent(Event<Integer> event) throws Exception {
                System.out.println(Thread.currentThread().getName() + ":" + event.getData());
                TimeUnit.SECONDS.sleep(1);
            }
        });
        for (int i = 0; i < 60; i++) {
            disruptorService.submit(i);
        }
        TimeUnit.SECONDS.sleep(25);
        disruptorService.shutdown();
//        int nThread = 4;
//        Executor executor = Executors.newFixedThreadPool(nThread);
//        Disruptor<Event<Integer>> disruptor = new Disruptor<>(Event::new, 1024, executor);
//        disruptor.handleEventsWithWorkerPool(createWorkHanlders(nThread));
//        disruptor.start();
//        for (int i = 0; i < 50; i++) {
//            RingBuffer<Event<Integer>> ringBuffer;
//            ringBuffer = disruptor.getRingBuffer();
//            final int finalI = i;
//            ringBuffer.publishEvent((event, sequence, arg0) -> event.setData(finalI), i);
//        }
//        disruptor.shutdown();
    }

    private static WorkHandler[] createWorkHanlders(int i) {
        WorkHandler[] arr = new WorkHandler[i];
        for (int j = 0; j < i; j++) {
            arr[j] = new WorkHandler<Event<Integer>>() {
                @Override
                public void onEvent(Event<Integer> event) throws Exception {
                    System.out.println(Thread.currentThread().getName() + ": " + event.getData());
                }
            };
        }

        return arr;
    }


}
