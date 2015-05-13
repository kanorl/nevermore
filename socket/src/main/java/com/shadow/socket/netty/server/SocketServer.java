package com.shadow.socket.netty.server;

import com.shadow.common.util.execution.LoggedExecution;
import com.shadow.common.util.thread.NamedThreadFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.ThreadFactory;

/**
 * @author nevermore on 2014/11/26
 */
@Component
public final class SocketServer implements ApplicationListener<ContextStartedEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SocketServer.class);

    @Value("${server.socket.port}")
    private int port;
    @Autowired
    private ApplicationContext applicationContext;

    private Channel channel;
    private EventLoopGroup parentGroup;
    private EventLoopGroup childGroup;
    private ServerInitializer serverInitializer;

    @PostConstruct
    private void initialize() {
        parentGroup = newEventLoopGroup(1, new NamedThreadFactory("Socket Acceptor"));
        childGroup = newEventLoopGroup(0, new NamedThreadFactory("Socket I/O"));// use DEFAULT_EVENT_LOOP_THREADS
        serverInitializer = applicationContext.getAutowireCapableBeanFactory().createBean(ServerInitializer.class);
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
                    .childHandler(serverInitializer);
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

        LoggedExecution.forName("关闭Socket服务").executeSilently(() -> {
            channel.close().syncUninterruptibly();
            parentGroup.shutdownGracefully();
            childGroup.shutdownGracefully();
            applicationContext.getAutowireCapableBeanFactory().destroyBean(serverInitializer);
        });
    }

    private EventLoopGroup newEventLoopGroup(int nThread, ThreadFactory threadFactory) {
        return Epoll.isAvailable() ? new EpollEventLoopGroup(nThread, threadFactory) : new NioEventLoopGroup(nThread, threadFactory);
    }
}
