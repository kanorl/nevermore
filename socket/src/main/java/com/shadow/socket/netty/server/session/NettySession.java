package com.shadow.socket.netty.server.session;

import com.shadow.socket.core.session.AbstractSession;
import io.netty.channel.Channel;

/**
 * @author nevermore on 2014/11/26
 */
public final class NettySession extends AbstractSession {
    private Channel channel;
    private long id;

    private NettySession() {
    }

    public static NettySession valueOf(long id, Channel channel) {
        NettySession nettySession = new NettySession();
        nettySession.id = id;
        nettySession.channel = channel;
        return nettySession;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void write(Object data) {
        channel.writeAndFlush(data);
    }
}