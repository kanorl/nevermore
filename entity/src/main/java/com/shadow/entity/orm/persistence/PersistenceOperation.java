package com.shadow.entity.orm.persistence;

import com.shadow.entity.IEntity;
import com.shadow.entity.orm.DataAccessor;

/**
 * 持久化操作
 *
 * @author nevermore on 2014/11/26.
 */
enum PersistenceOperation {
    SAVE {
        @Override
        public void perform(DataAccessor dataAccessor, IEntity<?> entity) {
            dataAccessor.save(entity);
        }
    },
    UPDATE {
        @Override
        public void perform(DataAccessor dataAccessor, IEntity<?> entity) {
            dataAccessor.update(entity);
        }
    },
    DELETE {
        @Override
        public void perform(DataAccessor dataAccessor, IEntity<?> entity) {
            dataAccessor.delete(entity);
        }
    };

    public abstract void perform(DataAccessor dataAccessor, IEntity<?> entity);
}
