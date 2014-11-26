package com.shadow.util.disruptor;

/**
 * @author nevermore on 2014/11/26
 */
public class DisruptorTest {

    public static void main(String[] args) {
        DisruptorService<Integer> disruptorService = DisruptorBuilder.newBuilder().threadCount(2).build(new DisruptorEventHandler());

        disruptorService.submit(1);
        disruptorService.submit(2);
        disruptorService.submit(3);

        System.out.println("-----------------------");

        System.out.println(disruptorService.remainEventCount());
        disruptorService.shutdown();

    }
}
