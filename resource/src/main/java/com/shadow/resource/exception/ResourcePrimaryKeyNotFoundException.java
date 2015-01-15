package com.shadow.resource.exception;

/**
 * @author nevermore on 2015/1/15
 */
public class ResourcePrimaryKeyNotFoundException extends RuntimeException {

    public ResourcePrimaryKeyNotFoundException(Class<?> resourceType) {
        super("找不到主键：" + resourceType.getName());
    }
}
