package com.shadow.socket.core.domain;

/**
 * @author nevermore on 2014/11/26
 */
public final class AttrValue<T> {
    private T value;

    public static <T> AttrValue valueOf(T value) {
        return new AttrValue<>(value);
    }

    private AttrValue(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }
}
