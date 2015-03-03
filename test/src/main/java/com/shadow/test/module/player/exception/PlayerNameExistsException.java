package com.shadow.test.module.player.exception;

import com.shadow.util.exception.CheckedException;

/**
 * @author nevermore on 2015/3/2
 */
public class PlayerNameExistsException extends CheckedException {
    @Override
    public int code() {
        return PlayerExceptionCode.PLAYER_NAME_EXISTS;
    }
}
