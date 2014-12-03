package com.shadow.socket.netty.server;

import com.shadow.socket.netty.server.handler.ServerHandler;
import com.shadow.socket.netty.server.handler.ServerInitializer;
import com.shadow.socket.netty.server.handler.ServerSessionHandler;
import com.shadow.util.thread.NamedThreadFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadFactory;

/**
 * @author nevermore on 2014/11/26
 */
@Component
public final class SocketServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(SocketServer.class);

    @Value("${server.socket.port}")
    private int port;
    @Value("{server.socket.pool.size:0")
    private int configPoolSize;
    @Autowired
    private ServerHandler handler;

    private Channel channel;
    private EventLoopGroup parentGroup;
    private EventLoopGroup childGroup;
    private EventExecutorGroup executors;
    private Map<String, ChannelHandler> filters;

    @PostConstruct
    private void initialize() {
        parentGroup = newEventLoopGroup(1, new NamedThreadFactory("Socket Acceptor"));
        childGroup = newEventLoopGroup(0, new NamedThreadFactory("Socket IO"));// use DEFAULT_EVENT_LOOP_THREADS
        executors = new DefaultEventExecutorGroup(Math.max(configPoolSize, Runtime.getRuntime().availableProcessors() * 2), new NamedThreadFactory(""));
        filters = filters();
    }

    public void start() {
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(parentGroup, childGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ServerInitializer(handler, executors, filters));
            b.option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_REUSEADDR, true);
            channel = b.bind(port).sync().channel();
            LOGGER.error("Server start listening on " + port);
        } catch (Exception e) {
            LOGGER.error("Start server failed.", e);
        }
    }

    public void shutdown() {
        if (channel != null && channel.isActive()) {
            channel.close().awaitUninterruptibly();
        }
        parentGroup.shutdownGracefully();
        childGroup.shutdownGracefully();
        executors.shutdownGracefully();
    }

    private Map<String, ChannelHandler> filters() {
        Map<String, ChannelHandler> filters = new HashMap<>();
        filters.put("session", new ServerSessionHandler());
        return filters;
    }

    private EventLoopGroup newEventLoopGroup(int nThread, ThreadFactory threadFactory) {
        return Epoll.isAvailable() ? new EpollEventLoopGroup(nThread, threadFactory) : new NioEventLoopGroup(nThread, threadFactory);
    }
}
