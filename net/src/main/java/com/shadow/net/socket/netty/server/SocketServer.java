package com.shadow.net.socket.netty.server;

import com.shadow.net.socket.netty.server.handler.ServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author nevermore on 2014/11/26
 */
public final class SocketServer {
    private final SocketServerBuilder builder;
    EventLoopGroup parentGroup;
    EventLoopGroup childGroup;

    private SocketServer(SocketServerBuilder builder) {
        this.builder = builder;
    }

    public void start() throws InterruptedException {
        parentGroup = Epoll.isAvailable() ? new EpollEventLoopGroup(builder.getParentThreads()) : new NioEventLoopGroup(builder.getParentThreads());
        childGroup = Epoll.isAvailable() ? new EpollEventLoopGroup(builder.getChildThreads()) : new NioEventLoopGroup(builder.getChildThreads());
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(parentGroup, childGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ServerInitializer());
            ChannelFuture f = b.bind(builder.getPort()).sync();
            f.channel().closeFuture().sync();
        } finally {
            shutdown();
        }
    }

    public void shutdown() {
        if (parentGroup != null) {
            parentGroup.shutdownGracefully();
        }
        if (childGroup != null) {
            childGroup.shutdownGracefully();
        }
    }

    static SocketServer create(SocketServerBuilder builder) {
        return new SocketServer(builder);
    }
}
