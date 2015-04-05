package com.shadow.common.util.execution;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author nevermore on 2015/1/21
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExecuteWithLog {

    public String taskName();

    public LogLevel logLevel() default LogLevel.INFO;
}
