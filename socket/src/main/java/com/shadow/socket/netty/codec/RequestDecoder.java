package com.shadow.socket.netty.codec;

import com.shadow.socket.core.domain.Command;
import com.shadow.socket.core.domain.ParameterContainer;
import com.shadow.socket.core.domain.Request;
import com.shadow.util.codec.ProtostuffCodec;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.nio.ByteOrder;

/**
 * @author nevermore on 2014/11/26
 */
public class RequestDecoder extends LengthFieldBasedFrameDecoder {

    public RequestDecoder() {
        super(ByteOrder.BIG_ENDIAN, 65535, 0, 4, 0, 4, false);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Request decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        if (frame == null) {
            return null;
        }

        int module = frame.readInt();
        int cmd = frame.readInt();
        Command command = Command.valueOf(module, cmd);

        byte[] data = new byte[frame.readableBytes()];
        frame.readBytes(data);

        ParameterContainer pc = new ParameterContainer();
        ProtostuffCodec.decode(data, pc);

        return Request.wrap(command, pc);
    }

    @Override
    protected ByteBuf extractFrame(ChannelHandlerContext ctx, ByteBuf buffer, int index, int length) {
        return buffer.slice(index, length);
    }
}
