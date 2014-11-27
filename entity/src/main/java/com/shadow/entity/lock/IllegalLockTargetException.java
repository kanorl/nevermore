package com.shadow.entity.lock;

/**
 * 非法的加锁对象异常
 *
 * @author nevermore on 2014/11/27.
 */
public class IllegalLockTargetException extends RuntimeException {

    public IllegalLockTargetException(String msg) {
        super(msg);
    }
}
