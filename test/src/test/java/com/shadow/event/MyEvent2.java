package com.shadow.event;

/**
 * @author nevermore on 2014/12/28.
 */
public class MyEvent2 implements Event {
    private int id;

    public MyEvent2(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
