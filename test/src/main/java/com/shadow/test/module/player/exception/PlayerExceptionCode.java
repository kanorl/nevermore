package com.shadow.test.module.player.exception;

import com.shadow.common.exception.CheckedExceptionCode;

/**
 * @author nevermore on 2015/3/2
 */
public interface PlayerExceptionCode extends CheckedExceptionCode {

    int PLAYER_NAME_EXISTS = 1;

    int PLAYER_NOT_EXISTS = 2;
}
