package com.shadow.entity.cache;

import javax.annotation.Nonnull;

import static java.util.Objects.requireNonNull;

/**
 * @author nevermore on 2015/1/6
 */
public final class IndexEntry {

    private String name;
    private Object value;

    private IndexEntry(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public static IndexEntry valueOf(@Nonnull String name, @Nonnull Object value) {
        return new IndexEntry(requireNonNull(name), requireNonNull(value));
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }
}
