package com.shadow.socket.netty.server.handler;

import com.shadow.socket.netty.codec.Message2RequestDecoder;
import com.shadow.socket.netty.codec.MessageDecoder;
import com.shadow.socket.netty.codec.MessageEncoder;
import com.shadow.socket.netty.codec.Response2MessageEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;

/**
 * @author nevermore on 2014/11/26
 */
public class ServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();

        p.addLast(new MessageDecoder());
        p.addLast(new Message2RequestDecoder());

        p.addLast(new MessageEncoder());
        p.addLast(new Response2MessageEncoder());

        p.addLast(new ServerSessionHandler());
        p.addLast(new DefaultEventExecutorGroup(4), new ServerHandler());
    }
}
