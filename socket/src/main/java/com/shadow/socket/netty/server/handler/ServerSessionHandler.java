package com.shadow.socket.netty.server.handler;

import com.shadow.socket.netty.session.NettySessionManager;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author nevermore on 2014/11/30.
 */
@ChannelHandler.Sharable
public class ServerSessionHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        NettySessionManager.bindSession(ctx.channel());
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        NettySessionManager.unbindSession(ctx.channel());
        super.channelInactive(ctx);
    }
}
