package com.shadow.schedule;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author nevermore on 2015/2/11
 */
@Component
public class ScheduledBeanPostProcessor implements BeanPostProcessor, ApplicationContextAware {

    @Autowired
    private Scheduler scheduler;
    private ApplicationContext ctx;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        ReflectionUtils.doWithMethods(bean.getClass(), method -> {
            if (method.getParameterCount() > 0) {
                throw new UnsupportedOperationException("定时方法不能带参数。");
            }

            Scheduled scheduled = method.getAnnotation(Scheduled.class);
            String cron = scheduled.valueType().value2Cron(scheduled.value(), ctx);
            NamedTask task = createNameTask(bean, method, scheduled.name());
            scheduler.schedule(task, cron);
        }, method -> method.isAnnotationPresent(Scheduled.class));
        return bean;
    }

    private NamedTask createNameTask(Object bean, Method method, String taskName) {
        return new NamedTask() {
            @Override
            public String getName() {
                return taskName;
            }

            @Override
            public void run() {
                ReflectionUtils.makeAccessible(method);
                try {
                    method.invoke(bean);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }
}
