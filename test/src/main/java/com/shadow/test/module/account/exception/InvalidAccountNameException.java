package com.shadow.test.module.account.exception;

import com.shadow.util.exception.CheckedException;

/**
 * @author nevermore on 2015/3/2
 */
public class InvalidAccountNameException extends CheckedException {

	private static final long serialVersionUID = -8524901672182887337L;

	public InvalidAccountNameException(String accountName) {
        super(accountName);
    }

    @Override
    public int code() {
        return AccountExceptionCode.INVALID_ACCOUNT_NAME;
    }
}
