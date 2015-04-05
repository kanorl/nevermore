package com.shadow.socket.core.annotation;

import java.lang.annotation.*;

/**
 * @author nevermore on 2014/11/26
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface HandlerMethod {
    byte cmd();
}
