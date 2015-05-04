package com.shadow.entity.db.persistence;

import com.shadow.common.util.codec.JsonUtil;
import com.shadow.entity.IEntity;
import com.shadow.entity.cache.CachedEntity;
import com.shadow.entity.db.Crud;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.slf4j.helpers.MessageFormatter.format;

/**
 * @author nevermore on 2015/1/1.
 */
class PersistenceTask implements Runnable {
    public static final Logger LOGGER = LoggerFactory.getLogger(PersistenceTask.class);

    private PersistenceObj obj;
    private Crud crud;

    public PersistenceTask(PersistenceObj obj, Crud crud) {
        this.obj = obj;
        this.crud = crud;
    }

    public static PersistenceTask newTask(PersistenceObj obj, Crud crud) {
        return new PersistenceTask(obj, crud);
    }

    @Override
    public void run() {
        final IEntity<?> entity = obj.getEntity();
        PersistenceOperation operation = obj.getOperation();

        boolean isCachedEntity = entity instanceof CachedEntity;

        if (isCachedEntity && ((CachedEntity) entity).isPersisted()) {
            return;
        }

        IEntity<?> actualEntity = isCachedEntity ? ((CachedEntity) entity).getEntity() : entity;
        try {
            operation.perform(crud, actualEntity);
        } catch (Exception e) {
            if (!(isCachedEntity && ((CachedEntity) entity).isPersisted())) {
                LOGGER.error(format("入库失败[class={}, entity={}, operation={}]", actualEntity.getClass().getSimpleName(), JsonUtil.toJson(actualEntity)).getMessage(), operation, e);
                return;
            }
        }
        if (isCachedEntity) {
            ((CachedEntity) entity).postPersist();// update DB Version to Edit Version
        }

        obj.getCallback().ifPresent(Runnable::run);
    }
}
