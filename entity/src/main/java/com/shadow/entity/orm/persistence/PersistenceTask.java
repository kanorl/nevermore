package com.shadow.entity.orm.persistence;

import com.shadow.entity.IEntity;
import com.shadow.entity.orm.DataAccessor;
import com.shadow.entity.proxy.VersionedEntityProxy;
import com.shadow.util.codec.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.slf4j.helpers.MessageFormatter.format;

/**
 * @author nevermore on 2015/1/1.
 */
class PersistenceTask implements Runnable {
    public static final Logger LOGGER = LoggerFactory.getLogger(PersistenceTask.class);

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
        final IEntity<?> entity = obj.getEntity();
        PersistenceOperation operation = obj.getOperation();

        boolean isVersionProxy = entity instanceof VersionedEntityProxy;

        IEntity<?> actualEntity = isVersionProxy ? ((VersionedEntityProxy) entity).getEntity() : entity;

        if (isVersionProxy && ((VersionedEntityProxy) entity).isPersisted()) {
            return;
        }

        try {
            operation.perform(dataAccessor, actualEntity);
        } catch (Exception e) {
            if (isVersionProxy && ((VersionedEntityProxy) entity).isPersisted()) {
                return;
            }
            LOGGER.error(format("入库失败[class={}, entity={}]", actualEntity.getClass().getSimpleName(), JsonUtil.toJson(actualEntity)).getMessage(), e);
            return;
        }
        if (isVersionProxy) {
            ((VersionedEntityProxy) entity).postPersist();// update DB Version to Edit Version
        }

        obj.getCallback().run();
    }
}
