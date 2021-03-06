package com.shadow.socket.client.socket.codec;

import com.shadow.socket.core.domain.Command;
import com.shadow.socket.core.domain.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.nio.ByteOrder;

public class MessageDecoder extends LengthFieldBasedFrameDecoder {

    public MessageDecoder() {
        super(ByteOrder.BIG_ENDIAN, 65535, 0, 4, 0, 4, false);
    }

    @Override
    protected Message decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        if (frame == null) {
            return null;
        }

        short module = frame.readShort();
        byte cmd = frame.readByte();
        Command command = Command.valueOf(module, cmd);

        byte[] data = new byte[frame.readableBytes()];
        frame.readBytes(data);

        return Message.valueOf(command, data);
    }

    @Override
    protected ByteBuf extractFrame(ChannelHandlerContext ctx, ByteBuf buffer, int index, int length) {
        return buffer.slice(index, length);
    }
}
