package com.shadow.schedule;

import org.springframework.context.ApplicationContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author nevermore on 2015/2/11
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Scheduled {

    /**
     * 任务名称
     *
     * @return
     */
    String name();

    /**
     * cron表达式内容
     *
     * @return
     */
    String value();

    /**
     * value的类型，默认值为{@link com.shadow.schedule.Scheduled.ValueType#CRON_BEAN_ID}
     *
     * @return
     */
    ValueType valueType() default ValueType.CRON_BEAN_ID;

    enum ValueType {

        /**
         * value的类型为Spring管理的beanId，需要根据beanId取得对应的cron表达式
         */
        CRON_BEAN_ID {
            @Override
            public String value2Cron(String value, ApplicationContext ctx) {
                return ctx.getBean(value, String.class);
            }
        },

        /**
         * 说明value的类型是cron表达式
         */
        CRON {
            @Override
            public String value2Cron(String value, ApplicationContext ctx) {
                return value;
            }
        };

        public abstract String value2Cron(String value, ApplicationContext ctx);
    }
}
