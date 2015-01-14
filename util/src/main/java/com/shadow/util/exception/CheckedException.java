package com.shadow.util.exception;

/**
 * @author nevermore on 2015/1/14
 */
public abstract class CheckedException extends RuntimeException {

    public CheckedException(String msg) {
        super(msg);
    }

    public abstract int code();
}