package com.shadow.socket.netty.codec;

import com.shadow.socket.core.domain.Response;
import com.shadow.util.codec.ProtostuffCodec;
import com.shadow.util.lang.ArrayUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author nevermore on 2014/11/26
 */
public class ResponseEncoder extends MessageToByteEncoder<Response> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Response response, ByteBuf out) throws Exception {
        byte[] data = ProtostuffCodec.encode(response.getResult());
        byte[] commandBytes = response.getCommand().toBytes();
        
        int length = commandBytes.length + data.length;
        byte[] result = new byte[length];
        int offset = 0;
        ArrayUtil.fill(commandBytes, result, offset);
        offset += commandBytes.length;
        ArrayUtil.fill(data, result, offset);

        out.writeInt(result.length);
        out.writeBytes(result);
    }
}
