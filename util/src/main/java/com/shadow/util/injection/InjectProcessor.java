package com.shadow.util.injection;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * @author nevermore on 2015/1/16
 */
public interface InjectProcessor<T> extends BeanPostProcessor {

    void inject(T bean);

    @SuppressWarnings("unchecked")
    @Override
    default Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        inject((T) bean);
        return bean;
    }

    @Override
    default Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
