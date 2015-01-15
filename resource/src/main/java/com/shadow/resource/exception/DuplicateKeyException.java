package com.shadow.resource.exception;

import org.slf4j.helpers.MessageFormatter;

/**
 * @author nevermore on 2015/1/15
 */
public class DuplicateKeyException extends RuntimeException {

    public DuplicateKeyException(Class<?> resourceType, Object key) {
        super(MessageFormatter.format("资源[{}]存在重复的主键[{}]", resourceType.getSimpleName(), key).getMessage());
    }
}
