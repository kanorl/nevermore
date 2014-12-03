package com.shadow.socket.client.socket.codec;

import com.shadow.socket.core.domain.Command;
import com.shadow.socket.core.domain.Response;
import com.shadow.socket.core.domain.Result;
import com.shadow.util.codec.ProtostuffCodec;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.nio.ByteOrder;

/**
 * Created by LiangZengle on 2014/9/15.
 */
public class ResponseDecoder extends LengthFieldBasedFrameDecoder {

    public ResponseDecoder() {
        super(ByteOrder.BIG_ENDIAN, 65535, 0, 4, 0, 4, false);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        if (frame == null) {
            return null;
        }

        int module = frame.readInt();
        int cmd = frame.readInt();
        Command command = Command.valueOf(module, cmd);

        byte[] data = new byte[frame.readableBytes()];
        frame.readBytes(data);

        Result<?> result = new Result<>();
        ProtostuffCodec.decode(data, result);
        return Response.valueOf(command, result);
    }

    @Override
    protected ByteBuf extractFrame(ChannelHandlerContext ctx, ByteBuf buffer, int index, int length) {
        return buffer.slice(index, length);
    }
}
