package com.shadow.entity.lock.exception;

/**
 * 非法的自动加锁方法异常
 *
 * @author nevermore on 2014/12/5
 */
public class IllegalAutoLockedMethodException extends RuntimeException {

	private static final long serialVersionUID = 7831642312238447504L;

	public IllegalAutoLockedMethodException(String msg) {
        super(msg);
    }
}
