package com.shadow.socket.core.annotation.support;

import com.shadow.socket.core.domain.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;

/**
 * @author nevermore on 2014/11/27
 */
public class UnknownRequestProcessor extends RequestProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(UnknownRequestProcessor.class);

    protected UnknownRequestProcessor() {
        super();
    }

    @Override
    public Object handle(@Nonnull Request request) throws InvocationTargetException, IllegalAccessException {
        LOGGER.error("No processor for " + request.getCommand());
        return null;
    }

    @Override
    public boolean isOmitResponse() {
        return true;
    }
}
