package com.shadow.socket.netty.codec;

import com.shadow.socket.core.domain.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author nevermore on 2015/1/1.
 */
public class MessageEncoder extends MessageToByteEncoder<Message> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        byte[] commandBytes = msg.getCommand().toBytes();
        byte[] data = msg.getBody();
        out.writeInt(commandBytes.length + data.length);
        out.writeBytes(commandBytes);
        out.writeBytes(data);
    }
}
