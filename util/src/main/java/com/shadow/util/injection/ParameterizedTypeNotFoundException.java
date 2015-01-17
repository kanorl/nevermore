package com.shadow.util.injection;

import org.slf4j.helpers.MessageFormatter;

import java.lang.reflect.Field;

/**
 * @author nevermore on 2015/1/16.
 */
public class ParameterizedTypeNotFoundException extends RuntimeException {

    public ParameterizedTypeNotFoundException(Field field) {
        super(MessageFormatter.format("找不到参数化类型: class={}, filed={}", field.getDeclaringClass().getName(), field.getName()).getMessage());
    }
}
