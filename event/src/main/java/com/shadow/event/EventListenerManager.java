package com.shadow.event;

import com.shadow.util.lang.ReflectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.unmodifiableSet;

/**
 * 事件分发器
 *
 * @author nevermore on 2014/12/28.
 */
@Component
public class EventListenerManager extends InstantiationAwareBeanPostProcessorAdapter implements ApplicationListener<ContextRefreshedEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventListenerManager.class);

    private final Map<EventListener<Event>, Set<Class<? extends Event>>> listenerScope = new HashMap<>();
    private final Map<Class<? extends Event>, Set<EventListener<Event>>> eventScope = new HashMap<>();

    /**
     * 获取该事件的所有监听器
     *
     * @param eventType 事件类型
     * @return a unmodified set
     */
    @Nonnull
    Set<EventListener<Event>> getListeners(Class<? extends Event> eventType) {
        return eventScope.getOrDefault(eventType, Collections.emptySet());
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
        return bean;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ReflectUtil.getAllSubTypesOf(Event.class).forEach(eventType -> {
            Set<EventListener<Event>> listeners = listenerScope.keySet().stream().filter(listener -> listenerScope.get(listener).contains(eventType)).collect(Collectors.toSet());
            eventScope.put(eventType, unmodifiableSet(listeners));

            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("事件[{}]的监听器: {}", eventType.getName(), listeners);
            }
        });
        listenerScope.clear();
    }
}
