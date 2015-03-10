package com.shadow.socket.netty.server;

import com.shadow.socket.netty.codec.HeaderAppender;
import com.shadow.socket.netty.codec.MessageDecoder;
import com.shadow.socket.netty.codec.MessageEncoder;
import com.shadow.socket.netty.server.handler.ServerHandler;
import com.shadow.socket.netty.server.session.ServerSessionHandler;
import com.shadow.util.thread.NamedThreadFactory;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author nevermore on 2014/11/26
 */
public class ServerInitializer extends ChannelInitializer<SocketChannel> {

    @Value("${server.socket.pool.size:0}")
    private int poolSize;
    @Autowired
    private ServerHandler handler;
    @Autowired
    private ServerSessionHandler sessionHandler;

    private EventExecutorGroup executors;

    @PostConstruct
    private void init() {
        executors = new DefaultEventExecutorGroup(Math.max(poolSize, Runtime.getRuntime().availableProcessors() + 1), new NamedThreadFactory("Socket Request Handler"));
    }

    @PreDestroy
    private void destroy() {
        executors.shutdownGracefully();
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();

        // decoder
        p.addLast(new MessageDecoder());

        // encoder
        p.addLast(new HeaderAppender());
        p.addLast(new MessageEncoder());

        // filter
        p.addLast("sessionHandler", sessionHandler);

        // handler
        p.addLast(executors, "handler", handler);
    }
}
