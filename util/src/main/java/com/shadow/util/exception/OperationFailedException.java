package com.shadow.util.exception;

/**
 * @author nevermore on 2015/3/2
 */
public class OperationFailedException extends CheckedException {
    @Override
    public int code() {
        return CheckedExceptionCode.OPERATION_FAILED;
    }
}
