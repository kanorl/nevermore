package com.shadow.entity.cache;

import com.shadow.entity.orm.DataAccessor;
import com.shadow.entity.orm.persistence.PersistenceEventHandler;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

/**
 * @author nevermore on 2014/11/26.
 */
@Component
public final class EntityCacheServiceManagerFactoryBean implements FactoryBean<EntityCacheServiceManager> {

    @Autowired
    private DataAccessor dataAccessor;
    @Autowired
    private PersistenceEventHandler persistenceEventHandler;
    @Value("${server.cache.size.default}")
    private int defaultCacheSize;

    private EntityCacheServiceManager serviceManager;

    @Override
    public EntityCacheServiceManager getObject() throws Exception {
        serviceManager = new EntityCacheServiceManager(dataAccessor, persistenceEventHandler, defaultCacheSize);
        return serviceManager;
    }

    @Override
    public Class<?> getObjectType() {
        return EntityCacheServiceManager.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @PreDestroy
    private void shutdown() {
        serviceManager.shutdown();
    }
}
