package com.shadow.entity.db.persistence;

import com.shadow.entity.IEntity;

import java.util.Optional;

/**
 * 持久化对象
 *
 * @author nevermore on 2014/11/26.
 */
final class PersistenceObj {
    private IEntity<?> entity;
    private PersistenceOperation operation;
    private Optional<Runnable> callback;

    private PersistenceObj(IEntity<?> entity, PersistenceOperation operation, Runnable callback) {
        this.entity = entity;
        this.operation = operation;
        this.callback = Optional.ofNullable(callback);
    }

    static <T extends IEntity<?>> PersistenceObj saveOf(T t, Runnable callback) {
        return new PersistenceObj(t, PersistenceOperation.SAVE, callback);
    }

    static <T extends IEntity<?>> PersistenceObj updateOf(T t, Runnable callback) {
        return new PersistenceObj(t, PersistenceOperation.UPDATE, callback);
    }

    static <T extends IEntity<?>> PersistenceObj deleteOf(T t, Runnable callback) {
        return new PersistenceObj(t, PersistenceOperation.DELETE, callback);
    }

    IEntity<?> getEntity() {
        return entity;
    }

    PersistenceOperation getOperation() {
        return operation;
    }

    Optional<Runnable> getCallback() {
        return callback;
    }
}
