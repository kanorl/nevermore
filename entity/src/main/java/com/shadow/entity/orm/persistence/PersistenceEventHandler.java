package com.shadow.entity.orm.persistence;

import com.lmax.disruptor.WorkHandler;
import com.shadow.entity.IEntity;
import com.shadow.entity.orm.DataAccessor;
import com.shadow.entity.proxy.EntityProxy;
import com.shadow.util.disruptor.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 持久化事件Disruptor处理器
 *
 * @author nevermore on 2014/11/26.
 */
@Component
public final class PersistenceEventHandler implements WorkHandler<Event<PersistenceObj>> {

    @Autowired
    private DataAccessor dataAccessor;


    @Override
    public void onEvent(Event<PersistenceObj> e) throws Exception {
        IEntity<?> entity = e.getData().getEntity();
        PersistenceOperation operation = e.getData().getOperation();

        if (entity instanceof EntityProxy) {
            entity = ((EntityProxy) entity).getEntity();
        }

        operation.perform(dataAccessor, entity);
        e.getData().getCallback().run();
    }
}
