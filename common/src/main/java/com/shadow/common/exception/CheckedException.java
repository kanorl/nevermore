package com.shadow.common.exception;

/**
 * @author nevermore on 2015/1/14
 */
public abstract class CheckedException extends RuntimeException {

    private static final long serialVersionUID = 6141612821453714608L;
    private final int code;

    public CheckedException(int code) {
        this.code = code;
    }

    public CheckedException(int code, String msg) {
        super(msg);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
