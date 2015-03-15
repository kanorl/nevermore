package com.shadow.util.exception;

/**
 * @author nevermore on 2015/3/2
 */
public class OperationFailedException extends CheckedException {

    private static final long serialVersionUID = -815410314593435805L;

    public OperationFailedException() {
        super(CheckedExceptionCode.OPERATION_FAILED);
    }
}
