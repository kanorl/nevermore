package com.shadow.socket.core.exception;

/**
 * @author nevermore on 2015/1/14
 */
public abstract class CheckedException extends RuntimeException {

    protected CheckedException(String msg) {
        super(msg);
    }

    public abstract int code();
}
