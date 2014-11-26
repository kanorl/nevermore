package com.shadow.entity.orm.persistence;

import com.shadow.entity.IEntity;

/**
 * 持久化对象
 *
 * @author nevermore on 2014/11/26.
 */
public final class PersistenceObj {
    private IEntity<?> entity;
    private PersistenceOperation operation;
    private Runnable callback;

    public static <T extends IEntity<?>> PersistenceObj saveOf(T t, Runnable callback) {
        PersistenceObj obj = new PersistenceObj();
        obj.entity = t;
        obj.operation = PersistenceOperation.SAVE;
        obj.callback = callback;
        return obj;
    }

    public static <T extends IEntity<?>> PersistenceObj updateOf(T t, Runnable callback) {
        PersistenceObj obj = new PersistenceObj();
        obj.entity = t;
        obj.operation = PersistenceOperation.UPDATE;
        obj.callback = callback;
        return obj;
    }

    public static <T extends IEntity<?>> PersistenceObj deleteOf(T t, Runnable callback) {
        PersistenceObj obj = new PersistenceObj();
        obj.entity = t;
        obj.operation = PersistenceOperation.DELETE;
        obj.callback = callback;
        return obj;
    }

    public IEntity<?> getEntity() {
        return entity;
    }

    public PersistenceOperation getOperation() {
        return operation;
    }

    public Runnable getCallback() {
        return callback;
    }
}
