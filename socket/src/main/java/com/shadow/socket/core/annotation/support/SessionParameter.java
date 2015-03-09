package com.shadow.socket.core.annotation.support;


import com.shadow.socket.core.domain.Request;

import java.lang.annotation.Annotation;

/**
 * @author nevermore on 2014/11/26
 */
public final class SessionParameter extends MethodParameter<Annotation> {

    public SessionParameter() {
        super(null);
    }

    @Override
    public Object getValue(Request request) {
        return request.getSession();
    }
}
