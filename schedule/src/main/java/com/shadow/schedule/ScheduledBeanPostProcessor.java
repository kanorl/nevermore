package com.shadow.schedule;

import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author nevermore on 2015/2/11
 */
@Component
public class ScheduledBeanPostProcessor implements BeanPostProcessor, ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private Scheduler scheduler;
    @Autowired
    private ApplicationContext ctx;

    private Map<ScheduledTask, String> tasks = new HashMap<>();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        ReflectionUtils.doWithMethods(bean.getClass(), method -> {
            if (method.getParameterCount() > 0) {
                throw new UnsupportedOperationException("定时方法不能带参数。");
            }

            Scheduled scheduled = method.getAnnotation(Scheduled.class);
            String cron = scheduled.valueType().value2Cron(scheduled.value(), ctx);
            ScheduledTask task = createScheduledTask(scheduled.name(), method, bean);
            tasks.put(task, cron);
        }, method -> method.isAnnotationPresent(Scheduled.class));
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    private ScheduledTask createScheduledTask(String name, Method method, Object bean) {
        return new ScheduledTask() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public void run() {
                try {
                    ReflectionUtils.makeAccessible(method);
                    method.invoke(bean);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    LoggerFactory.getLogger(ScheduledTask.class).error(e.getMessage(), e);
                    throw new RuntimeException(e);
                }
            }
        };
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        for (Map.Entry<ScheduledTask, String> entry : tasks.entrySet()) {
            scheduler.schedule(entry.getKey(), entry.getValue());
        }
        tasks = Collections.emptyMap();
    }
}
