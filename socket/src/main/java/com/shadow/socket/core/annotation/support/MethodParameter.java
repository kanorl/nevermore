package com.shadow.socket.core.annotation.support;


import com.shadow.socket.core.domain.Request;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 方法参数
 *
 * @author nevermore on 2014/11/26.
 */
public abstract class MethodParameter<A extends Annotation> {
    protected Method method;
    protected Class<?> paramType;
    protected A annotation;

    public MethodParameter(Method method, Class<?> paramType, A annotation) {
        this.method = method;
        this.paramType = paramType;
        this.annotation = annotation;
    }

    public abstract Object getValue(Request request);
}
