package com.shadow.entity.cache;

import com.shadow.entity.orm.DataAccessor;
import com.shadow.entity.orm.persistence.PersistenceEventHandler;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author nevermore on 2014/11/26.
 */
@Component
public final class EntityCacheServiceManagerFactoryBean implements FactoryBean<EntityCacheServiceManager> {

    @Autowired
    private DataAccessor dataAccessor;
    @Autowired
    private PersistenceEventHandler persistenceEventHandler;

    @Override
    public EntityCacheServiceManager getObject() throws Exception {
        return new EntityCacheServiceManager(dataAccessor, persistenceEventHandler);
    }

    @Override
    public Class<?> getObjectType() {
        return EntityCacheServiceManager.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
