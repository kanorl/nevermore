package com.shadow.socket.core.domain;

import com.shadow.util.codec.ProtostuffCodec;
import com.shadow.util.lang.ArrayUtil;

/**
 * @author nevermore on 2014/11/26.
 */
public final class Response {
    private Command command;
    private Result result;

    public static Response valueOf(Command command, Result<?> result) {
        Response response = new Response();
        response.command = command;
        response.result = result;
        return response;
    }

    public byte[] toBytes() {
        byte[] data = ProtostuffCodec.encode(result);
        byte[] commandBytes = command.toBytes();
        int length = commandBytes.length + data.length;
        byte[] result = new byte[length];
        int offset = 0;
        ArrayUtil.fill(commandBytes, result, offset);
        offset += commandBytes.length;
        ArrayUtil.fill(data, result, offset);
        return result;
    }

    public Command getCommand() {
        return command;
    }

    public Result getResult() {
        return result;
    }
}
