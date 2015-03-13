package com.shadow.test.module.account.exception;

import com.shadow.util.exception.CheckedException;

/**
 * @author nevermore on 2015/3/2
 */
public class AccountNotExistsException extends CheckedException {

	private static final long serialVersionUID = -6583087066643289428L;

	@Override
    public int code() {
        return AccountExceptionCode.ACCOUNT_NOT_EXISTS;
    }
}
