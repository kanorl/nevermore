package com.shadow.socket.core.annotation.support;

import com.shadow.socket.core.domain.Request;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author nevermore on 2014/11/26.
 */
public class RequestProcessor {
    private Method method;
    private Object invoker;
    private MethodParameter[] params;
    private boolean omitResponse;

    protected RequestProcessor() {
    }

    public static RequestProcessor valueOf(@Nonnull Method method, @Nonnull Object invoker, @Nonnull MethodParameter[] params) {
        ReflectionUtils.makeAccessible(checkNotNull(method));
        RequestProcessor requestProcessor = new RequestProcessor();
        requestProcessor.method = method;
        requestProcessor.invoker = checkNotNull(invoker);
        requestProcessor.params = checkNotNull(params);
        requestProcessor.omitResponse = method.getReturnType() == Void.class;
        return requestProcessor;
    }

    public Object handle(@Nonnull Request request) throws InvocationTargetException {
        Object[] args = getArgs(checkNotNull(request));
        try {
            return method.invoke(invoker, args);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("should never happen.");
        }
    }

    public boolean isOmitResponse() {
        return omitResponse;
    }

    private Object[] getArgs(Request request) {
        if (ArrayUtils.isEmpty(params)) {
            return null;
        }
        Object[] args = new Object[params.length];
        for (int i = 0; i < args.length; i++) {
            args[i] = params[i].getValue(request);
        }
        return args;
    }
}

