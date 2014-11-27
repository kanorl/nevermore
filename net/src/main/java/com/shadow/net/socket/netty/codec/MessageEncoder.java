package com.shadow.net.socket.netty.codec;

import com.shadow.net.socket.core.domain.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author nevermore on 2014/11/26
 */
public class MessageEncoder extends MessageToByteEncoder<Message> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        byte[] data = msg.toBytes();
        int length = data.length;
        out.writeInt(length);
        out.writeBytes(data);
    }
}
