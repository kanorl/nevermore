package com.shadow.test.module.player.exception;

import com.shadow.util.exception.CheckedException;

/**
 * @author nevermore on 2015/3/2
 */
public class PlayerNotExistsException extends CheckedException {
    @Override
    public int code() {
        return PlayerExceptionCode.PLAYER_NOT_EXISTS;
    }
}
