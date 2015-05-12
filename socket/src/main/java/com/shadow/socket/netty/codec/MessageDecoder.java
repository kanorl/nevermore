package com.shadow.socket.netty.codec;

import com.shadow.socket.core.domain.Command;
import com.shadow.socket.core.domain.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteOrder;

/**
 * @author nevermore on 2015/1/1.
 */
public class MessageDecoder extends LengthFieldBasedFrameDecoder {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageDecoder.class);

    private static final byte MESSAGE_START_FLAG = -1;

    public MessageDecoder() {
        super(ByteOrder.BIG_ENDIAN, 1024, 1, 4, 0, 5, true);
    }

    @Override
    protected Message decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        if (in.readableBytes() < 1) {
            return null;
        }
        int startFlag = in.getByte(0);
        if (startFlag != MESSAGE_START_FLAG) {
            ctx.close();
            LOGGER.error("连接关闭: 非法的消息开始标志[{}]", startFlag);
            throw new DecoderException("Unexpected message start flag: expected[" + MESSAGE_START_FLAG + "], given[" + startFlag + "]");
        }

        ByteBuf frame;
        try {
            frame = (ByteBuf) super.decode(ctx, in);
        } catch (Exception e) {
            ctx.close();
            LOGGER.error("连接关闭: {}", e.getMessage());
            throw new DecoderException(e);
        }

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
