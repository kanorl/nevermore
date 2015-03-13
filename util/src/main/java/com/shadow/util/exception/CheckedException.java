package com.shadow.util.exception;

/**
 * @author nevermore on 2015/1/14
 */
public abstract class CheckedException extends RuntimeException {

	private static final long serialVersionUID = 6141612821453714608L;

	public CheckedException() {
    }

    public CheckedException(String msg) {
        super(msg);
    }

    public abstract int code();
}
