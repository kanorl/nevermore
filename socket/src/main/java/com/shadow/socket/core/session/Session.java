package com.shadow.socket.core.session;


import com.shadow.socket.core.domain.AttrKey;
import com.shadow.socket.core.domain.AttrValue;

import java.util.Optional;

/**
 * @author nevermore on 2014/11/26.
 */
public interface Session {

    long getId();

    void send(Object data);

    <T> Optional<T> getAttr(AttrKey key);

    <T> void setAttr(AttrKey key, T value);

    <T> Optional<AttrValue<T>> setAttrIfAbsent(AttrKey key, T value);
}
