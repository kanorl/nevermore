package com.shadow.entity.lock.exception;

/**
 * 非法的加锁对象异常
 *
 * @author nevermore on 2014/11/27.
 */
public class IllegalLockTargetException extends RuntimeException {

	private static final long serialVersionUID = 6716213347022133359L;

	public IllegalLockTargetException(String msg) {
        super(msg);
    }
}
