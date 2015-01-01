package com.shadow.socket.client.socket;

import com.shadow.socket.client.Client;
import com.shadow.socket.core.domain.Message;
import com.shadow.socket.core.domain.Result;
import com.shadow.util.codec.JsonUtil;
import com.shadow.util.codec.ProtostuffCodec;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ClientHandler extends SimpleChannelInboundHandler<Message> {


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        Result<?> result = new Result<>();
        ProtostuffCodec.decode(msg.getBody(), result);
        Client.INSTANCE.onPushReceived(msg.getCommand().getModule(), msg.getCommand().getCmd(), JsonUtil.toJson(result).getBytes());
    }
}
