package com.shadow.entity.orm.persistence;

import com.shadow.entity.IEntity;
import com.shadow.entity.orm.DataAccessor;
import com.shadow.entity.proxy.EntityProxy;

/**
 * @author nevermore on 2015/1/1.
 */
class PersistenceTask implements Runnable {
    private PersistenceObj obj;
    private DataAccessor dataAccessor;

    public PersistenceTask(PersistenceObj obj, DataAccessor dataAccessor) {
        this.obj = obj;
        this.dataAccessor = dataAccessor;
    }

    public static PersistenceTask newTask(PersistenceObj obj, DataAccessor dataAccessor) {
        return new PersistenceTask(obj, dataAccessor);
    }

    @Override
    public void run() {
        IEntity<?> entity = obj.getEntity();
        PersistenceOperation operation = obj.getOperation();

        if (entity instanceof EntityProxy) {
            entity = ((EntityProxy) entity).getEntity();
        }

        operation.perform(dataAccessor, entity);
        obj.getCallback().run();
    }
}
