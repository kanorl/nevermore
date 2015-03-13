package com.shadow.test.module.account.exception;

import com.shadow.util.exception.CheckedException;

/**
 * @author nevermore on 2015/3/1
 */
public class IllegalPlatformException extends CheckedException {

	private static final long serialVersionUID = -2677616541461262800L;

	@Override
    public int code() {
        return AccountExceptionCode.ILLEGAL_PLATFORM;
    }
}
