package com.shadow.entity.cache;

import com.shadow.entity.orm.DataAccessor;
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
    @Value("${server.cache.size.default}")
    private int defaultCacheSize;
    @Value("${server.persistence.pool.size:1}")
    private int persistencePoolSize;

    private EntityCacheServiceManager serviceManager;

    @Override
    public EntityCacheServiceManager getObject() throws Exception {
        serviceManager = new EntityCacheServiceManager(dataAccessor, defaultCacheSize, persistencePoolSize);
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
