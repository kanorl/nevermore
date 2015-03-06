package com.shadow.socket.netty.codec;

import com.shadow.socket.core.domain.Message;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * @author nevermore on 2015/1/1.
 */
public class MessageEncoder extends MessageToMessageEncoder<Message> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> out) throws Exception {
        out.add(Unpooled.wrappedBuffer(msg.getCommand().bytes(), msg.getBody()));
    }
}
