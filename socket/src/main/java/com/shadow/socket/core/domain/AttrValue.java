package com.shadow.socket.core.domain;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * @author nevermore on 2014/11/26
 */
public final class AttrValue<T> {
    private final T value;

    public static <T> AttrValue<T> valueOf(@Nonnull T value) {
        return new AttrValue<>(Objects.requireNonNull(value));
    }

    private AttrValue(T value) {
        this.value = value;
    }

    @Nonnull
    public T get() {
        return value;
    }
}
