package com.shadow.socket.core.domain;


import com.shadow.util.lang.ArrayUtil;

/**
 * @author nevermore on 2014/11/26.
 */
public final class Message {
    private Command command;
    private byte[] data;

    public static Message valueOf(Command command, byte[] data) {
        Message msg = new Message();
        msg.command = command;
        msg.data = data;
        return msg;
    }

    public Command getCommand() {
        return command;
    }

    public byte[] getData() {
        return data;
    }

    public byte[] toBytes() {
        byte[] commandBytes = command.toBytes();
        int length = commandBytes.length + data.length;
        byte[] result = new byte[length];
        int offset = 0;
        ArrayUtil.fill(commandBytes, result, offset);
        offset += commandBytes.length;
        ArrayUtil.fill(data, result, offset);
        return result;
    }
}
