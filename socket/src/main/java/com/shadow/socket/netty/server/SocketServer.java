package com.shadow.socket.netty.server;

import com.shadow.socket.netty.server.handler.ServerHandler;
import com.shadow.socket.netty.server.session.ServerSessionHandler;
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
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadFactory;

/**
 * @author nevermore on 2014/11/26
 */
@Component
public final class SocketServer implements ApplicationListener<ContextStartedEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SocketServer.class);

    @Value("${server.socket.port}")
    private int port;
    @Value("${server.socket.pool.size:0}")
    private int poolSize;
    @Autowired
    private ServerHandler handler;
    @Autowired
    private ServerSessionHandler sessionHandler;

    private Channel channel;
    private EventLoopGroup parentGroup;
    private EventLoopGroup childGroup;
    private EventExecutorGroup executors;
    private Map<String, ChannelHandler> filters;

    @PostConstruct
    private void initialize() {
        parentGroup = newEventLoopGroup(1, new NamedThreadFactory("Socket Acceptor"));
        childGroup = newEventLoopGroup(0, new NamedThreadFactory("Socket I/O"));// use DEFAULT_EVENT_LOOP_THREADS
        executors = new DefaultEventExecutorGroup(Math.max(poolSize, Runtime.getRuntime().availableProcessors() * 2), new NamedThreadFactory("Socket Request Handler"));
        filters = filters();
    }

    @Override
    public void onApplicationEvent(ContextStartedEvent event) {
        start();
    }

    private void start() {
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(parentGroup, childGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ServerInitializer(handler, executors, filters));
            b.option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_REUSEADDR, true);
            channel = b.bind(port).sync().channel();
            LOGGER.error("服务器已启动，开始监听端口: " + port);
        } catch (Exception e) {
            LOGGER.error("服务器启动失败", e);
        }
    }

    @PreDestroy
    private void shutdown() {
        if (channel == null) {
            return;
        }

        LOGGER.error("开始 [关闭Socket服务]");

        channel.close().syncUninterruptibly();
        parentGroup.shutdownGracefully();
        childGroup.shutdownGracefully();
        executors.shutdownGracefully();

        LOGGER.error("完成 [关闭Socket服务]");
    }

    private Map<String, ChannelHandler> filters() {
        Map<String, ChannelHandler> filters = new HashMap<>();
        filters.put("session", sessionHandler);
        return filters;
    }

    private EventLoopGroup newEventLoopGroup(int nThread, ThreadFactory threadFactory) {
        return Epoll.isAvailable() ? new EpollEventLoopGroup(nThread, threadFactory) : new NioEventLoopGroup(nThread, threadFactory);
    }
}
