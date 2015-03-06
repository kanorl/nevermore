package com.shadow.socket.netty.server.session;

import com.shadow.socket.core.session.AbstractSession;
import io.netty.channel.Channel;

/**
 * @author nevermore on 2014/11/26
 */
public final class NettySession extends AbstractSession {
    private final transient Channel channel;
    private final long id;

    private NettySession(long id, Channel channel) {
        this.id = id;
        this.channel = channel;
    }

    public static NettySession valueOf(long id, Channel channel) {
        return new NettySession(id, channel);
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void send(Object data) {
        channel.writeAndFlush(data);
    }
}
