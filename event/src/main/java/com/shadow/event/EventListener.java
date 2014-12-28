package com.shadow.event;

/**
 * 事件监听器接口
 *
 * @author nevermore on 2014/12/28.
 */
@FunctionalInterface
public interface EventListener<E extends Event> {

    void onEvent(E event);

}
