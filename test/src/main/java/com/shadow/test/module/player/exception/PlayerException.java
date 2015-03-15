package com.shadow.test.module.player.exception;

import com.shadow.util.exception.CheckedException;

/**
 * @author nevermore on 2015/3/15.
 */
public class PlayerException extends CheckedException {

    private static final long serialVersionUID = 2189613079723800388L;

    public PlayerException(int code) {
        super(code);
    }
}
