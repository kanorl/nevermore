package com.shadow.socket.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldPrepender;
import org.springframework.stereotype.Component;

/**
 * @author nevermore on 2015/3/6
 */
@Component
@ChannelHandler.Sharable
public class HeaderPrepender extends LengthFieldPrepender {

    public HeaderPrepender() {
        super(4, 0, false);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
        super.encode(ctx, msg, out);
    }
}
