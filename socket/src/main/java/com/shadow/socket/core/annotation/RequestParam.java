package com.shadow.socket.core.annotation;

import java.lang.annotation.*;

/**
 * 请求中的参数
 *
 * @author nevermore on 2014/11/26
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface RequestParam {
    public String value();
}
