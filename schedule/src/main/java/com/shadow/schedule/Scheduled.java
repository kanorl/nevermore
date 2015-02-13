package com.shadow.schedule;

import com.shadow.util.ApplicationContextUtil;

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

    public String name();

    public String value();

    public ValueType valueType() default ValueType.BEAN_NAME;

    public enum ValueType {
        BEAN_NAME {
            @Override
            public String value2Cron(String value) {
                return ApplicationContextUtil.getBean(value, String.class);
            }
        },
        EXPRESSION {
            @Override
            public String value2Cron(String value) {
                return value;
            }
        };

        public abstract String value2Cron(String value);
    }
}