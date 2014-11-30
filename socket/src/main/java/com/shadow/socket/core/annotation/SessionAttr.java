package com.shadow.socket.core.annotation;


import com.shadow.socket.core.domain.AttrKey;

import java.lang.annotation.*;

/**
 * @author nevermore on 2014/11/26
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface SessionAttr {
    public AttrKey value();
}
