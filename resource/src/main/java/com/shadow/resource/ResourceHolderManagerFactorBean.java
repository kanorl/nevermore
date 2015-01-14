package com.shadow.resource;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author nevermore on 2015/1/14
 */
@Component
public class ResourceHolderManagerFactorBean implements FactoryBean<ResourceHolderManager>, ApplicationContextAware {

    private ResourceHolderManager resourceHolderManager;
    private ApplicationContext applicationContext;

    @PostConstruct
    private void init() {
        resourceHolderManager = applicationContext.getAutowireCapableBeanFactory().createBean(ResourceHolderManager.class);
    }

    @Override
    public ResourceHolderManager getObject() throws Exception {
        return resourceHolderManager;
    }

    @Override
    public Class<?> getObjectType() {
        return ResourceHolderManager.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
