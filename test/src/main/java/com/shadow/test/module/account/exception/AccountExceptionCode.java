package com.shadow.test.module.account.exception;

import com.shadow.common.exception.CheckedExceptionCode;

/**
 * @author nevermore on 2015/3/1
 */
public interface AccountExceptionCode extends CheckedExceptionCode {
    int ACCOUNT_NAME_EXISTS = 1;
    int ILLEGAL_PLATFORM = 2;
    int ILLEGAL_SERVER = 3;
    int INVALID_ACCOUNT_NAME = 4;
    int ACCOUNT_NOT_EXISTS = 5;
}
