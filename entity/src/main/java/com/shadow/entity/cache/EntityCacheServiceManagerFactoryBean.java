package com.shadow.entity.cache;

import com.shadow.entity.orm.DataAccessor;
import com.shadow.entity.orm.persistence.PersistenceEventHandler;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

/**
 * @author nevermore on 2014/11/26.
 */
/*
 * 添加depends-on，解决异常：
 * org.springframework.beans.factory.BeanCreationNotAllowedException: Error creating bean with name 'transactionManager': Singleton bean creation not allowed while the singletons of this factory are in destruction (Do not request a bean from a BeanFactory in a destroy method implementation!)
 * 保证transactionManager销毁顺序在本类之后
 */
@DependsOn("transactionManager")
@Component
public final class EntityCacheServiceManagerFactoryBean implements FactoryBean<EntityCacheServiceManager> {

    @Autowired
    private DataAccessor dataAccessor;
    @Autowired
    private PersistenceEventHandler persistenceEventHandler;
    @Value("${server.cache.size.default}")
    private int defaultCacheSize;
    @Value("${server.persistence.thread.num}")
    private int nThread;

    private EntityCacheServiceManager serviceManager;

    @Override
    public EntityCacheServiceManager getObject() throws Exception {
        serviceManager = new EntityCacheServiceManager(dataAccessor, persistenceEventHandler, defaultCacheSize, nThread);
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
