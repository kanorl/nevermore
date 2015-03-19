package com.shadow.entity.orm.persistence;

import com.shadow.entity.IEntity;

/**
 * 持久化处理器
 *
 * @author nevermore on 2014/11/26.
 */
public interface PersistenceProcessor<T extends IEntity<?>> {

    default void save(T t) {
        save(t, null);
    }

    void save(T t, Runnable callback);

    default void update(T t) {
        update(t, null);
    }

    void update(T t, Runnable callback);

    default void delete(T t) {
        delete(t, null);
    }

    void delete(T t, Runnable callback);

    long remainTasks();
}
