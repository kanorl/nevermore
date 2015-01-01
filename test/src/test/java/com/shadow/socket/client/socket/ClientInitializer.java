package com.shadow.socket.client.socket;

import com.shadow.socket.client.socket.codec.MessageDecoder;
import com.shadow.socket.client.socket.codec.RequestEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class ClientInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        p.addLast(new MessageDecoder());

        p.addLast(new RequestEncoder());

        p.addLast(new ClientHandler());
    }
}
