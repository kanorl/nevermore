package com.shadow.socket.core.session;


import com.shadow.socket.core.domain.AttrKey;
import com.shadow.socket.core.domain.AttrValue;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author nevermore on 2014/11/26.
 */
public abstract class AbstractSession implements Session {
    protected Map<AttrKey, AttrValue<?>> attributes = new EnumMap<>(AttrKey.class);

    public <T> void setAttr(AttrKey key, T value) {
        attributes.put(key, AttrValue.valueOf(value));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Optional<AttrValue<T>> setAttrIfAbsent(AttrKey key, T value) {
        return Optional.ofNullable((AttrValue<T>) attributes.putIfAbsent(key, AttrValue.valueOf(value)));
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> getAttr(AttrKey key) {
        AttrValue<?> attrValue = attributes.get(key);
        return Optional.ofNullable(attrValue == null ? null : (T) attrValue.get());
    }
}
