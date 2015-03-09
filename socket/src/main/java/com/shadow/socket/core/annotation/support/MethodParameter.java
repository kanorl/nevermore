package com.shadow.socket.core.annotation.support;


import com.shadow.socket.core.domain.Request;

import java.lang.annotation.Annotation;

/**
 * 方法参数
 *
 * @author nevermore on 2014/11/26.
 */
public abstract class MethodParameter<A extends Annotation> {
    protected A annotation;

    public MethodParameter(A annotation) {
        this.annotation = annotation;
    }

    public abstract Object getValue(Request request);
}
