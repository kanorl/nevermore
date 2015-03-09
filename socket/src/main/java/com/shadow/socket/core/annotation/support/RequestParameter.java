package com.shadow.socket.core.annotation.support;


import com.shadow.socket.core.annotation.RequestParam;
import com.shadow.socket.core.domain.Request;

/**
 * @author nevermore on 2014/11/26.
 */
public final class RequestParameter extends MethodParameter<RequestParam> {

    private final String paramName;

    public RequestParameter(String paramName) {
        super(null);
        this.paramName = paramName;
    }

    @Override
    public Object getValue(Request request) {
        return request.getBody().get(paramName);
    }
}
