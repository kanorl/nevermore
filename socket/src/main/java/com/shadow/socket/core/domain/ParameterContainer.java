package com.shadow.socket.core.domain;

import java.util.Map;

/**
 * @author nevermore on 2014/11/26.
 */
public final class ParameterContainer {
    private Map<String, Object> params;

    public ParameterContainer() {
    }

    public static ParameterContainer valueOf(Map<String, Object> params) {
        ParameterContainer pc = new ParameterContainer();
        pc.params = params;
        return pc;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public ParameterContainer put(String key, Object value) {
        params.put(key, value);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) params.get(key);
    }
}
