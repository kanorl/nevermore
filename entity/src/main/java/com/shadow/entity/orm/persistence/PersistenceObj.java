package com.shadow.entity.orm.persistence;

import com.shadow.entity.IEntity;

/**
 * 持久化对象
 *
 * @author nevermore on 2014/11/26.
 */
final class PersistenceObj {
    private IEntity<?> entity;
    private PersistenceOperation operation;
    private Runnable callback;

    private PersistenceObj(IEntity<?> entity, PersistenceOperation operation, Runnable callback) {
        this.entity = entity;
        this.operation = operation;
        this.callback = callback;
    }

    public static <T extends IEntity<?>> PersistenceObj saveOf(T t, Runnable callback) {
        return new PersistenceObj(t, PersistenceOperation.SAVE, callback);
    }

    public static <T extends IEntity<?>> PersistenceObj updateOf(T t, Runnable callback) {
        return new PersistenceObj(t, PersistenceOperation.UPDATE, callback);
    }

    public static <T extends IEntity<?>> PersistenceObj deleteOf(T t, Runnable callback) {
        return new PersistenceObj(t, PersistenceOperation.DELETE, callback);
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
