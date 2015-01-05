package com.shadow.entity.cache;

import com.shadow.entity.cache.annotation.CacheSize;
import com.shadow.entity.orm.DataAccessor;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author nevermore on 2014/11/26.
 */
@Component
public final class EntityCacheManagerFactoryBean implements FactoryBean<EntityCacheManager> {

    @Autowired
    private DataAccessor dataAccessor;
    @Value("${server.cache.size.minimum}")
    private int minimumCacheSize;
    @Value("${server.cache.size.default}")
    private int defaultCacheSize;
    @Value("${server.persistence.pool.size:1}")
    private int persistencePoolSize;

    private EntityCacheManager serviceManager;

    @PostConstruct
    private void init() {
        CacheSize.Size.MINIMUM.set(minimumCacheSize);
        CacheSize.Size.DEFAULT.set(defaultCacheSize);
    }

    @Override
    public EntityCacheManager getObject() throws Exception {
        serviceManager = new EntityCacheManager(dataAccessor, persistencePoolSize);
        return serviceManager;
    }

    @Override
    public Class<?> getObjectType() {
        return EntityCacheManager.class;
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
