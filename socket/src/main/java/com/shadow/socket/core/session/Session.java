package com.shadow.socket.core.session;


import com.shadow.socket.core.domain.AttrKey;

/**
 * @author nevermore on 2014/11/26.
 */
public interface Session<K> {

    K getId();

    <T> void write(T data);

    <T> T getAttr(AttrKey key);

    <T> void setAttr(AttrKey key, T value);
}
