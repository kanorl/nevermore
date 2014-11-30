package com.shadow.socket.client.socket;

import com.shadow.socket.client.Client;
import com.shadow.socket.core.domain.Response;
import com.shadow.util.codec.JsonUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by LiangZengle on 2014/8/30.
 */
public class ClientHandler extends SimpleChannelInboundHandler<Response> {


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Response msg) throws Exception {
        Client.INSTANCE.onPushReceived(msg.getCommand().getModule(), msg.getCommand().getCmd(), JsonUtil.toJson(msg.getResult()).getBytes());
    }
}
