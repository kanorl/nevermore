package com.shadow.entity.lock.exception;

/**
 * 非法的自动加锁方法异常
 *
 * @author nevermore on 2014/12/5
 */
public class IllegalAutoLockedMethodException extends RuntimeException {

    public IllegalAutoLockedMethodException(String msg) {
        super(msg);
    }
}
