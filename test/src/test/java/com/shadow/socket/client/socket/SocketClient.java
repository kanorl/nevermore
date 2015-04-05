package com.shadow.socket.client.socket;

import com.shadow.common.util.codec.JsonUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public final class SocketClient {
    private EventLoopGroup group;
    private Channel channel;

    public void connect(String host, int port) {
        group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ClientInitializer());
            ChannelFuture f = b.connect(host, port).sync();
            channel = f.channel();
//            channel.closeFuture().sync();
        } catch (Exception e) {
            group.shutdownGracefully();
        }
    }

    public void shutdown() {
        group.shutdownGracefully();
    }

    public void send(Object obj) {
        if (channel.isWritable()) {
            channel.writeAndFlush(obj);
            System.err.println("send->" + JsonUtil.toJson(obj));
        }
    }
}
