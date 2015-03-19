package com.shadow.entity.cache;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author nevermore on 2014/11/26.
 */
@Component
public final class EntityCacheManagerFactoryBean implements FactoryBean<EntityCacheManager> {

    @Autowired
    private ApplicationContext applicationContext;
    private EntityCacheManager entityCacheManager;

    @PostConstruct
    private void init() {
        entityCacheManager = applicationContext.getAutowireCapableBeanFactory().createBean(EntityCacheManager.class);
    }

    @Override
    public EntityCacheManager getObject() throws Exception {
        return entityCacheManager;
    }

    @Override
    public Class<?> getObjectType() {
        return EntityCacheManager.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
