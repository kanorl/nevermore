package com.shadow.net.socket.core.annotation.support;


import com.shadow.net.socket.core.annotation.Session;
import com.shadow.net.socket.core.domain.Request;

import java.lang.reflect.Method;

/**
 * @author nevermore on 2014/11/26
 */
public final class SessionParameter extends MethodParameter<Session> {

    public SessionParameter(Method method, Class<?> paramType, Session annotation) {
        super(method, paramType, annotation);
    }

    @Override
    public Object getValue(Request request) {
        return request.getSession();
    }
}
