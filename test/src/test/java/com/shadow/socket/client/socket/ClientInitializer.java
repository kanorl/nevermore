package com.shadow.socket.client.socket;

import com.shadow.socket.client.socket.codec.RequestEncoder;
import com.shadow.socket.client.socket.codec.ResponseDecoder;
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
        p.addLast(new ResponseDecoder());

        p.addLast(new RequestEncoder());

        p.addLast(new ClientHandler());
    }
}
