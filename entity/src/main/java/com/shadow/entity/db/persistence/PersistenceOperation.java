package com.shadow.entity.db.persistence;

import com.shadow.entity.IEntity;
import com.shadow.entity.db.Crud;

/**
 * 持久化操作
 *
 * @author nevermore on 2014/11/26.
 */
enum PersistenceOperation {
    SAVE {
        @Override
        public void perform(Crud crud, IEntity<?> entity) {
            crud.save(entity);
        }
    },
    UPDATE {
        @Override
        public void perform(Crud crud, IEntity<?> entity) {
            crud.update(entity);
        }
    },
    DELETE {
        @Override
        public void perform(Crud crud, IEntity<?> entity) {
            crud.delete(entity);
        }
    };

    public abstract void perform(Crud crud, IEntity<?> entity);
}
