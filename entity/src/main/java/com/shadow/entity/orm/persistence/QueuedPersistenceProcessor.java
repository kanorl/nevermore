package com.shadow.entity.orm.persistence;

import com.shadow.entity.IEntity;
import com.shadow.entity.orm.DataAccessor;
import com.shadow.util.concurrent.ExecutorUtil;
import com.shadow.util.lang.MathUtil;
import com.shadow.util.thread.NamedThreadFactory;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 队列式持久化处理器
 *
 * @author nevermore on 2014/11/26.
 */
public class QueuedPersistenceProcessor<T extends IEntity<?>> implements PersistenceProcessor<T> {

    private final ExecutorService[] executors;
    private final DataAccessor dataAccessor;
    private final String name;
    private static final Runnable DEFAULT_CALLBACK = () -> {
    };

    public QueuedPersistenceProcessor(DataAccessor dataAccessor, String entityName, int nThread) {
        this.dataAccessor = dataAccessor;
        this.name = entityName + "持久化";
        executors = new ExecutorService[MathUtil.ensurePowerOf2(nThread)];
        for (int i = 0; i < executors.length; i++) {
            executors[i] = Executors.newSingleThreadExecutor(new NamedThreadFactory(name));
        }
    }

    private ExecutorService executor(T t) {
        return executors[t.getId().hashCode() & (executors.length - 1)];
    }

    @Override
    public void save(T t) {
        save(t, DEFAULT_CALLBACK);
    }

    @Override
    public void save(T t, Runnable callback) {
        executor(t).submit(PersistenceTask.newTask(PersistenceObj.saveOf(t, callback), dataAccessor));
    }

    @Override
    public void update(T t) {
        update(t, DEFAULT_CALLBACK);
    }

    @Override
    public void update(T t, Runnable callback) {
        executor(t).submit(PersistenceTask.newTask(PersistenceObj.updateOf(t, callback), dataAccessor));
    }

    @Override
    public void delete(T t) {
        delete(t, DEFAULT_CALLBACK);
    }

    @Override
    public void delete(T t, Runnable callback) {
        executor(t).submit(PersistenceTask.newTask(PersistenceObj.deleteOf(t, callback), dataAccessor));
    }

    @Override
    public long remainTasks() {
        return -1;
    }

    @Override
    public void shutdown() {
        Arrays.stream(executors).forEach(executor -> ExecutorUtil.shutdownAndAwaitTermination(executor, name, 10, TimeUnit.MINUTES));
    }
}
