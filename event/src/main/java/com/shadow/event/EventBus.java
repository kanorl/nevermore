package com.shadow.event;

import com.shadow.util.thread.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 事件总线
 *
 * @author nevermore on 2014/12/28.
 */
@Component
public class EventBus {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventBus.class);


    @Autowired
    private EventDispatcher eventDispatcher;
    @Value("${server.event.thread.num:0}")
    private int nThread;

    private ExecutorService executorService;

    @PostConstruct
    private void init() {
        if (nThread < 1) {
            nThread = Runtime.getRuntime().availableProcessors();
        }
        LOGGER.error("事件处理线程池大小: " + nThread);
        executorService = Executors.newFixedThreadPool(nThread, new NamedThreadFactory("事件处理线程"));
    }

    @PreDestroy
    private void destroy() {
        executorService.shutdown();
    }

    public void post(Event event) {
        executorService.submit(() -> eventDispatcher.dispatch(event));
    }
}