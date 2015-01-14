package com.shadow.entity.cache;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author nevermore on 2014/11/26.
 */
@Component
public final class EntityCacheManagerFactoryBean implements FactoryBean<EntityCacheManager>, ApplicationContextAware {

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

    @PreDestroy
    private void shutdown() {
        entityCacheManager.shutdown();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}