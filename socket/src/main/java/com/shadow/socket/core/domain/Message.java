package com.shadow.socket.core.domain;

import com.shadow.util.codec.JsonUtil;

/**
 * @author nevermore on 2015/1/1.
 */
public final class Message {
    private Command command;
    private byte[] body;

    public static Message valueOf(Command command, byte[] body) {
        Message msg = new Message();
        msg.command = command;
        msg.body = body;
        return msg;
    }

    public Command getCommand() {
        return command;
    }

    public byte[] getBody() {
        return body;
    }

    @Override
    public String toString() {
        return JsonUtil.toJson(this);
    }
}
