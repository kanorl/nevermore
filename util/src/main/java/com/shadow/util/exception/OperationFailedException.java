package com.shadow.util.exception;

/**
 * @author nevermore on 2015/3/2
 */
public class OperationFailedException extends CheckedException {

	private static final long serialVersionUID = -815410314593435805L;

	@Override
    public int code() {
        return CheckedExceptionCode.OPERATION_FAILED;
    }
}
