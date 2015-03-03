package com.shadow.test.module.account.exception;

import com.shadow.util.exception.CheckedException;

/**
 * @author nevermore on 2015/3/1
 */
public class IllegalServerException extends CheckedException {
    @Override
    public int code() {
        return AccountExceptionCode.ILLEGAL_SERVER;
    }
}
