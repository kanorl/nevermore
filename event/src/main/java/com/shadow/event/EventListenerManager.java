package com.shadow.event;

import com.google.common.collect.*;
import com.shadow.common.util.lang.ReflectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 事件监听器管理
 *
 * @author nevermore on 2014/12/28.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
@Component
public class EventListenerManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventListenerManager.class);

    @Autowired
    private ApplicationContext ctx;

    private Multimap<Class<? extends Event>, EventListener> listeners = ImmutableListMultimap.of();

    @PostConstruct
    private void init() {
        Set<EventListener> allListeners = Sets.newHashSet(ctx.getBeansOfType(EventListener.class).values());
        Map<EventListener, Set<Class<? extends Event>>> listenedEvents = Maps.asMap(allListeners, listener -> Arrays.stream(listener.getClass().getGenericInterfaces())
                        .filter(type -> type instanceof ParameterizedType && EventListener.class.isAssignableFrom((Class<?>) ((ParameterizedType) type).getRawType()))
                        .flatMap(type -> {
                            Type[] pTypes = ((ParameterizedType) type).getActualTypeArguments();
                            Class<Event> clazz = (Class<Event>) pTypes[0];
                            Set<Class<? extends Event>> classes = ReflectUtil.getAllSubTypesOf(clazz);
                            classes.add(clazz);
                            return classes.stream();
                        })
                        .collect(Collectors.toSet())
        );

        Multimap<Class<? extends Event>, EventListener> eventListeners = ArrayListMultimap.create();
        ReflectUtil.getAllSubTypesOf(Event.class).forEach(eventType -> {
            List<EventListener> listeners = listenedEvents.keySet().stream().filter(key -> listenedEvents.get(key).contains(eventType)).collect(Collectors.toList());
            eventListeners.putAll(eventType, listeners);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("事件[{}]的监听器: [{}]", eventType.getName(), listeners.stream().map(listener -> listener.getClass().getSimpleName()));
            }
        });
        this.listeners = ImmutableListMultimap.copyOf(eventListeners);
    }

    /**
     * 获取该事件的所有监听器
     *
     * @param eventType 事件类型
     * @return a unmodifiable set
     */
    @Nonnull
    Collection<EventListener> getListeners(Class<? extends Event> eventType) {
        return listeners.get(eventType);
    }
}
