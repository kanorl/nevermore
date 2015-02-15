package com.shadow.socket.core.annotation.support;


import com.shadow.socket.core.domain.Request;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author nevermore on 2014/11/26
 */
public final class SessionParameter extends MethodParameter<Annotation> {

    public SessionParameter(Method method, Class<?> paramType) {
        super(method, paramType, null);
    }

    @Override
    public Object getValue(Request request) {
        return request.getSession();
    }
}
