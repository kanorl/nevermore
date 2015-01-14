package com.shadow.socket.core.exception;

/**
 * @author nevermore on 2015/1/14
 */
public abstract class CheckedException extends RuntimeException {

    private final int code;

    protected CheckedException(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
