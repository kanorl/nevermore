package com.shadow.entity.orm.persistence;

import com.shadow.entity.IEntity;

/**
 * 持久化处理器
 *
 * @author nevermore on 2014/11/26.
 */
public interface PersistenceProcessor<T extends IEntity<?>> {

    void save(T t);

    void save(T t, Runnable callback);

    void update(T t);

    void update(T t, Runnable callback);

    void delete(T t);

    void delete(T t, Runnable callback);

    long remainTasks();
}
