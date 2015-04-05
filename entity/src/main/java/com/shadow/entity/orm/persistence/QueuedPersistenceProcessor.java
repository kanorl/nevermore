package com.shadow.entity.orm.persistence;

import com.shadow.common.util.concurrent.ExecutorUtil;
import com.shadow.common.util.lang.MathUtil;
import com.shadow.common.util.thread.NamedThreadFactory;
import com.shadow.entity.IEntity;
import com.shadow.entity.orm.DataAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 队列式持久化处理器
 *
 * @author nevermore on 2014/11/26.
 */
@Component
public class QueuedPersistenceProcessor<T extends IEntity<?>> implements PersistenceProcessor<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(QueuedPersistenceProcessor.class);

    @Autowired
    private DataAccessor dataAccessor;
    @Value("${server.persistence.poolSize:0}")
    private int nThread;

    private ExecutorService[] executors;
    private static final NamedThreadFactory threadFactory = new NamedThreadFactory("队列持久化");

    @PostConstruct
    private void init() {
        if (nThread <= 0) {
            nThread = Runtime.getRuntime().availableProcessors() + 1;
        }
        int poolSize = MathUtil.ensurePowerOf2(nThread);
        executors = new ExecutorService[poolSize];
        for (int i = 0; i < executors.length; i++) {
            executors[i] = Executors.newFixedThreadPool(1, threadFactory);
        }
        LOGGER.error("队列持久化线程池大小={}", poolSize);
    }

    void submit(PersistenceObj obj) {
        executors[obj.getEntity().getId().hashCode() & (executors.length - 1)].submit(PersistenceTask.newTask(obj,
                dataAccessor));
    }

    @Override
    public void save(T t, Runnable callback) {
        submit(PersistenceObj.saveOf(t, callback));
    }

    @Override
    public void update(T t, Runnable callback) {
        submit(PersistenceObj.updateOf(t, callback));
    }

    @Override
    public void delete(T t, Runnable callback) {
        submit(PersistenceObj.deleteOf(t, callback));
    }

    @Override
    public long remainTasks() {
        return Arrays.stream(executors).filter(ThreadPoolExecutor.class::isInstance).mapToLong(value -> ((ThreadPoolExecutor) value).getTaskCount()).sum();
    }

    @PreDestroy
    public void shutdown() {
        for (int i = 0; i < executors.length; i++) {
            ExecutorUtil.shutdownAndAwaitTermination(executors[i], threadFactory.getName() + "-" + (i + 1));
        }
    }
}
