package com.shadow.entity.orm.persistence;

import com.lmax.disruptor.EventHandler;
import com.shadow.entity.IEntity;
import com.shadow.util.disruptor.DisruptorBuilder;
import com.shadow.util.disruptor.DisruptorService;
import com.shadow.util.disruptor.Event;

/**
 * 队列式持久化处理器
 *
 * @author nevermore on 2014/11/26.
 */
public class QueuedPersistenceProcessor<T extends IEntity<?>> implements PersistenceProcessor<T> {

    private final DisruptorService<PersistenceObj> disruptorService;
    private static final Runnable DEFAULT_CALLBACK = () -> {
    };

    public QueuedPersistenceProcessor(EventHandler<Event<PersistenceObj>> handler) {
        disruptorService = DisruptorBuilder.newBuilder().build(handler);
    }

    @Override
    public void save(T t) {
        save(t, DEFAULT_CALLBACK);
    }

    @Override
    public void save(T t, Runnable callback) {
        disruptorService.submit(PersistenceObj.saveOf(t, callback));
    }

    @Override
    public void update(T t) {
        update(t, DEFAULT_CALLBACK);
    }

    @Override
    public void update(T t, Runnable callback) {
        disruptorService.submit(PersistenceObj.updateOf(t, callback));
    }

    @Override
    public void delete(T t) {
        delete(t, DEFAULT_CALLBACK);
    }

    @Override
    public void delete(T t, Runnable callback) {
        disruptorService.submit(PersistenceObj.deleteOf(t, callback));
    }
}
