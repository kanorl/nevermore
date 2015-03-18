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
    private final Class<T> entityType;
    private static final NamedThreadFactory threadFactory = new NamedThreadFactory("队列持久化");

    public QueuedPersistenceProcessor(DataAccessor dataAccessor, int nThread, Class<T> entityType) {
        this.dataAccessor = dataAccessor;
        this.entityType = entityType;
        executors = new ExecutorService[MathUtil.ensurePowerOf2(nThread)];
        for (int i = 0; i < executors.length; i++) {
            executors[i] = Executors.newSingleThreadExecutor(threadFactory);
        }
    }

    private ExecutorService executor(T t) {
        return executors.length == 1 ? executors[0] : executors[t.getId().hashCode() & (executors.length - 1)];
    }

    @Override
    public void save(T t, Runnable callback) {
        executor(t).submit(PersistenceTask.newTask(PersistenceObj.saveOf(t, callback), dataAccessor));
    }

    @Override
    public void update(T t, Runnable callback) {
        executor(t).submit(PersistenceTask.newTask(PersistenceObj.updateOf(t, callback), dataAccessor));
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
        Arrays.stream(executors).forEach(executor -> ExecutorUtil.shutdownAndAwaitTermination(executor, entityType.getSimpleName() + threadFactory.getName(), 10, TimeUnit.MINUTES));
    }
}
