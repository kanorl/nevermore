package com.shadow.socket.core.domain;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * @author nevermore on 2014/11/26.
 */
public final class ParameterContainer {
    private final Map<String, Object> params;

    public ParameterContainer() {
        this(new HashMap<>());
    }

    public ParameterContainer(@Nonnull Map<String, Object> params) {
        requireNonNull(params);
        this.params = params;
    }

    public static ParameterContainer valueOf(Map<String, Object> params) {
        return new ParameterContainer(params);
    }

    /**
     * @return a unmodifiable map
     */
    public Map<String, Object> getAll() {
        return Collections.unmodifiableMap(params);
    }

    public ParameterContainer add(String key, Object value) {
        params.put(key, value);
        return this;
    }

    public ParameterContainer addAll(Map<String, Object> params) {
        this.params.putAll(params);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) params.get(key);
    }

    @Override
    public String toString() {
        return params.toString();
    }
}
