package com.shadow.event;

/**
 * @author nevermore on 2014/12/28.
 */
public class MyEvent implements Event {
    private String name;

    public MyEvent(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
