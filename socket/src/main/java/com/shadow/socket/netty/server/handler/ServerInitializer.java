package com.shadow.socket.netty.server.handler;

import com.shadow.socket.netty.codec.RequestDecoder;
import com.shadow.socket.netty.codec.ResponseEncoder;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.concurrent.EventExecutorGroup;

import java.util.Map;

/**
 * @author nevermore on 2014/11/26
 */
public class ServerInitializer extends ChannelInitializer<SocketChannel> {

    private ChannelHandler handler;
    private EventExecutorGroup executors;
    private Map<String, ChannelHandler> filters;

    public ServerInitializer(ChannelHandler handler, EventExecutorGroup executors, Map<String, ChannelHandler> filters) {
        this.handler = handler;
        this.executors = executors;
        this.filters = filters;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();

        for (Map.Entry<String, ChannelHandler> entry : filters.entrySet()) {
            p.addLast(entry.getKey(), entry.getValue());
        }

        p.addLast(new RequestDecoder());
        p.addLast(new ResponseEncoder());
        p.addLast(executors, "handler", handler);
    }
}
