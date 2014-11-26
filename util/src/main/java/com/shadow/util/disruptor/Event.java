package com.shadow.util.disruptor;

import com.shadow.util.codec.JsonUtil;

/**
 * Disruptor事件
 *
 * @author nevermore on 2014/11/26
 */
public final class Event<T> {

    private T data;


    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return JsonUtil.toJson(this);
    }
}
