package com.shadow.socket.client.socket;

import com.shadow.socket.client.socket.codec.Message2ResponseDecoder;
import com.shadow.socket.client.socket.codec.Request2MessageEncoder;
import com.shadow.socket.netty.codec.RequestDecoder;
import com.shadow.socket.netty.codec.ResponseEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * Created by LiangZengle on 2014/8/30.
 */
public class ClientInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        p.addLast(new RequestDecoder());
        p.addLast(new Message2ResponseDecoder());

        p.addLast(new ResponseEncoder());
        p.addLast(new Request2MessageEncoder());

        p.addLast(new ClientHandler());
    }
}
