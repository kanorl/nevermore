package com.shadow.socket.client.model;

public class Option {
    private String name;
    private Object value;


    public static Option valueOf(String name, Object value) {
        Option item = new Option();
        item.name = name;
        item.value = value;
        return item;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return name;
    }
}
