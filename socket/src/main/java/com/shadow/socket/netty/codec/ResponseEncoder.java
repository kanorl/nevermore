package com.shadow.socket.netty.codec;

import com.shadow.socket.core.domain.Response;
import com.shadow.util.codec.ProtostuffCodec;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author nevermore on 2014/11/26
 */
public class ResponseEncoder extends MessageToByteEncoder<Response> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Response response, ByteBuf out) throws Exception {
        byte[] commandBytes = response.getCommand().toBytes();
        byte[] data = ProtostuffCodec.encode(response.getResult());
        out.writeInt(commandBytes.length + data.length);
        out.writeBytes(commandBytes);
        out.writeBytes(data);
    }
}
