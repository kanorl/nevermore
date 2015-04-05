package com.shadow.test.module.account.exception;

import com.shadow.common.exception.CheckedException;

/**
 * @author nevermore on 2015/3/15.
 */
public class AccountException extends CheckedException {

    private static final long serialVersionUID = -8135732476383223994L;

    public AccountException(int code) {
        super(code);
    }
}
