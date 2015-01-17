package com.shadow.resource.exception;

/**
 * @author nevermore on 2015/1/14
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String msg) {
        super(msg);
    }

    public ResourceNotFoundException() {
        this("找不到资源");
    }
}
