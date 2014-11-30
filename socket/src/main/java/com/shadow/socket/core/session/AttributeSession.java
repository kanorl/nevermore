package com.shadow.socket.core.session;


import com.shadow.socket.core.domain.AttrKey;
import com.shadow.socket.core.domain.AttrValue;

import java.util.EnumMap;
import java.util.Map;

/**
 * @author nevermore on 2014/11/26.
 */
public abstract class AttributeSession<K> implements Session<K> {
    protected Map<AttrKey, AttrValue<?>> attributes = new EnumMap<>(AttrKey.class);

    public <T> void setAttr(AttrKey key, T value) {
        attributes.put(key, AttrValue.valueOf(value));
    }

    @SuppressWarnings("unchecked")
    public <T> T getAttr(AttrKey key) {
        return (T) attributes.get(key).get();
    }
}
