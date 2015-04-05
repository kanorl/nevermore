package com.shadow.resource.exception;

import com.shadow.common.exception.CheckedException;
import com.shadow.common.exception.CheckedExceptionCode;

/**
 * @author nevermore on 2015/1/14
 */
public class ResourceNotFoundException extends CheckedException {

    private static final long serialVersionUID = 1839361460868659015L;

    public ResourceNotFoundException() {
        this("找不到资源");
    }

    public ResourceNotFoundException(String msg) {
        super(CheckedExceptionCode.RESOURCE_NOT_FOUND, msg);
    }
}
