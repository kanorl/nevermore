package com.shadow.socket.netty.session;

import com.shadow.socket.core.session.AttributeSession;
import io.netty.channel.Channel;

/**
 * @author nevermore on 2014/11/26
 */
public final class NettySession extends AttributeSession<Long> {
    private Channel channel;
    private long id;

    private NettySession() {
    }

    static NettySession valueOf(long id, Channel channel) {
        NettySession nettySession = new NettySession();
        nettySession.id = id;
        nettySession.channel = channel;
        return nettySession;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public <T> void write(T data) {
        channel.writeAndFlush(data);
    }
}
