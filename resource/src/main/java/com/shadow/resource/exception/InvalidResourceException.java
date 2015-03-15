package com.shadow.resource.exception;

import org.slf4j.helpers.MessageFormatter;

/**
 * @author nevermore on 2015/1/15
 */
public class InvalidResourceException extends RuntimeException {

    private static final long serialVersionUID = -8365397922872065056L;

    public InvalidResourceException(Object resourceBean) {
        super(MessageFormatter.format("资源校验失败: class=[{}], value=[{}]", resourceBean.getClass().getSimpleName(), resourceBean).getMessage());
    }
}
