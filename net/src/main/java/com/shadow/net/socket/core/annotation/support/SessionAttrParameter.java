package com.shadow.net.socket.core.annotation.support;


import com.shadow.net.socket.core.annotation.SessionAttr;
import com.shadow.net.socket.core.domain.AttrKey;
import com.shadow.net.socket.core.domain.Request;
import com.shadow.net.socket.core.session.Session;

import java.lang.reflect.Method;

/**
 * @author nevermore on 2014/11/26
 */
public final class SessionAttrParameter extends MethodParameter<SessionAttr> {

    public SessionAttrParameter(Method method, Class<?> paramType, SessionAttr annotation) {
        super(method, paramType, annotation);
    }

    @Override
    public Object getValue(Request request) {
        AttrKey attrKey = annotation.value();
        Session session = request.getSession();
        return session.getAttr(attrKey);
    }
}
