package com.shadow.entity.orm.persistence;

import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.shadow.entity.IEntity;
import com.shadow.entity.cache.CachedEntity;
import com.shadow.util.concurrent.ExecutorUtil;
import com.shadow.util.thread.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * @author nevermore on 2015/3/19
 */
@Component
public class ScheduledPersistenceProcessor<T extends IEntity<?>> implements PersistenceProcessor<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledPersistenceProcessor.class);

    @Autowired
    private QueuedPersistenceProcessor<T> delegate;
    @Value("${server.persistence.interval:60}")
    private long interval;

    private LoadingCache<Class<?>, ConcurrentMap<Object, PersistenceObj>> cache = CacheBuilder
            .newBuilder().build(CacheLoader.from((Supplier<ConcurrentMap<Object, PersistenceObj>>)
                    ConcurrentHashMap::new));
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock w = lock.writeLock();
    private final Lock r = lock.readLock();
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor(new
            NamedThreadFactory("定时入库"));

    @PostConstruct
    private void init() {
        executorService.scheduleWithFixedDelay(new Task(), interval, interval, TimeUnit.SECONDS);
        LOGGER.error("定时入库间隔时间 {}s", interval);
    }

    @Override
    public void save(T t, Runnable callback) {
        r.lock();
        try {
            mapFor(t).put(t.getId(), PersistenceObj.saveOf(t, callback));
        } finally {
            r.unlock();
        }
    }

    @Override
    public void update(T t, Runnable callback) {
        r.lock();
        try {
            mapFor(t).putIfAbsent(t.getId(), PersistenceObj.updateOf(t, callback));
        } finally {
            r.unlock();
        }
    }

    @Override
    public void delete(T t, Runnable callback) {
        r.lock();
        try {
            mapFor(t).put(t.getId(), PersistenceObj.deleteOf(t, callback));
        } finally {
            r.unlock();
        }
    }

    @Override
    public long remainTasks() {
        return delegate.remainTasks();
    }

    @PreDestroy
    public void shutdown() {
        ExecutorUtil.shutdownAndAwaitTermination(executorService, "定时入库");
        new Task().run();
    }

    private ConcurrentMap<Object, PersistenceObj> mapFor(T t) {
        IEntity<?> entity = t instanceof CachedEntity ? ((CachedEntity) t).getEntity() : t;
        return cache.getUnchecked(entity.getClass());
    }

    private class Task implements Runnable {

        @Override
        public void run() {
            List<PersistenceObj> elements;
            w.lock();
            try {
                elements = cache.asMap().values().stream().flatMap(m -> m.values().stream()).collect(Collectors.toList());
                cache.invalidateAll();
            } finally {
                w.unlock();
            }
            elements.forEach(delegate::submit);

            LOGGER.info("本次定时入库提交了{}个任务", elements.size());
        }
    }
}
