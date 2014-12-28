package com.shadow.event;

import com.google.common.collect.Sets;
import com.shadow.util.lang.ReflectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.stereotype.Component;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * 事件分发器
 *
 * @author nevermore on 2014/12/28.
 */
@Component
public class EventDispatcher extends InstantiationAwareBeanPostProcessorAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventDispatcher.class);

    private final Map<EventListener<Event>, Set<Class<? extends Event>>> listenerScope = new HashMap<>();
    private final Map<Class<? extends Event>, Set<EventListener<Event>>> eventScope = new HashMap<>();

    <E extends Event> void dispatch(E event) {
        Set<EventListener<Event>> listeners = eventScope.get(event.getClass());
        if (listeners == null) {
            LOGGER.error("未被监听的事件[{}]", event.getClass().getName());
            return;
        }
        listeners.forEach(listener -> listener.onEvent(event));
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (!(bean instanceof EventListener)) {
            return bean;
        }
        Arrays.stream(bean.getClass().getGenericInterfaces())
                .filter(type -> type instanceof ParameterizedType && EventListener.class.isAssignableFrom((Class<?>) ((ParameterizedType) type).getRawType()))
                .forEach(type -> {
                    Type[] pTypes = ((ParameterizedType) type).getActualTypeArguments();
                    Class<Event> clazz = (Class<Event>) pTypes[0];
                    Set<Class<? extends Event>> scope = ReflectUtil.getAllSubTypesOf(clazz);
                    scope.add(clazz);
                    listenerScope.put((EventListener<Event>) bean, scope);
                });
        ReflectUtil.getAllSubTypesOf(Event.class).forEach(eventType -> {
            Set<EventListener<Event>> listeners = Sets.filter(listenerScope.keySet(), listener -> listenerScope.get(listener).contains(eventType));
            if (!listeners.isEmpty()) {
                eventScope.put(eventType, new HashSet<>(listeners));
            }
        });
        return bean;
    }
}
