package com.shadow.resource.exception;

/**
 * @author nevermore on 2015/1/15
 */
public class ResourcePrimaryKeyNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 39038079952281082L;

	public ResourcePrimaryKeyNotFoundException(Class<?> resourceType) {
        super("找不到主键：" + resourceType.getName());
    }
}
