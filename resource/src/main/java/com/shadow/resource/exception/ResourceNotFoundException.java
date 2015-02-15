package com.shadow.resource.exception;

import com.shadow.util.exception.CheckedException;
import com.shadow.util.exception.CheckedExceptionCode;

/**
 * @author nevermore on 2015/1/14
 */
public class ResourceNotFoundException extends CheckedException {

    public ResourceNotFoundException(String msg) {
        super(msg);
    }

    @Override
    public int code() {
        return CheckedExceptionCode.RESOURCE_NOT_FOUND;
    }

    public ResourceNotFoundException() {
        this("找不到资源");
    }
}
