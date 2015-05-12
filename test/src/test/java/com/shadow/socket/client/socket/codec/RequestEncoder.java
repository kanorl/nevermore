package com.shadow.socket.client.socket.codec;

import com.shadow.common.util.codec.ProtostuffCodec;
import com.shadow.socket.core.domain.Request;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class RequestEncoder extends MessageToByteEncoder<Request> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Request msg, ByteBuf out) throws Exception {
        byte[] commandBytes = msg.getCommand().bytes();
        byte[] data = ProtostuffCodec.getBytes(msg.getBody());

        out.writeByte(-1);
        out.writeInt(commandBytes.length + data.length);
        out.writeBytes(commandBytes);
        out.writeBytes(data);
    }
}
