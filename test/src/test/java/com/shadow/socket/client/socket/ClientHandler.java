package com.shadow.socket.client.socket;

import com.shadow.common.util.codec.JsonUtil;
import com.shadow.common.util.codec.ProtostuffCodec;
import com.shadow.socket.client.Client;
import com.shadow.socket.core.domain.Message;
import com.shadow.socket.core.domain.Result;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ClientHandler extends SimpleChannelInboundHandler<Message> {


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        Result<?> result = ProtostuffCodec.toObject(msg.getBody(), Result.class);
        Client.INSTANCE.onPushReceived(msg.getCommand().getModule(), msg.getCommand().getCmd(), JsonUtil.toJson(result).getBytes());
    }
}
