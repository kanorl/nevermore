package com.shadow.util.injection;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

/**
 * @author nevermore on 2015/1/16
 */
public interface InjectedAnnotationProcessor<T> extends BeanPostProcessor {

    void inject(Object target, Field field) throws IllegalAccessException;

    Class<T> fieldType();

    @SuppressWarnings("unchecked")
    @Override
    default Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        ReflectionUtils.doWithFields(bean.getClass(), field -> inject(bean, field), field -> field.isAnnotationPresent(Injected.class) && fieldType().isAssignableFrom(field.getType()));
        return bean;
    }

    @Override
    default Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
