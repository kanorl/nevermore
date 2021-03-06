package com.shadow.event;

import com.shadow.common.util.concurrent.ExecutorUtil;
import com.shadow.common.util.thread.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.Objects.requireNonNull;

/**
 * 事件总线
 *
 * @author nevermore on 2014/12/28.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
@Component
public class EventBus {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventBus.class);


    @Autowired
    private EventListenerManager eventListenerManager;
    @Value("${server.event.poolSize:0}")
    private int nThread;

    private ExecutorService executorService;

    @PostConstruct
    private void init() {
        if (nThread < 1) {
            nThread = Runtime.getRuntime().availableProcessors() + 1;
        }
        LOGGER.error("事件处理线程池大小: " + nThread);
        executorService = Executors.newFixedThreadPool(nThread, new NamedThreadFactory("事件处理"));
    }

    @PreDestroy
    private void shutdown() {
        ExecutorUtil.shutdownAndAwaitTermination(executorService, "事件处理");
    }

    public void post(@Nonnull Event event) {
        requireNonNull(event, "事件不能为null");
        Collection<EventListener> listeners = eventListenerManager.getListeners(event.getClass());
        if (listeners.isEmpty()) {
            LOGGER.info("未被监听的事件类型[{}]", event.getClass().getName());
            return;
        }
        listeners.forEach(listener -> executorService.submit(() -> listener.onEvent(event)));
    }

    public void syncPost(@Nonnull Event event) {
        requireNonNull(event, "事件不能为null");
        Collection<EventListener> listeners = eventListenerManager.getListeners(event.getClass());
        if (listeners.isEmpty()) {
            LOGGER.info("未被监听的事件类型[{}]", event.getClass().getName());
            return;
        }
        listeners.forEach(listener -> listener.onEvent(event));
    }
}
