package com.shadow.socket.netty.codec;

import io.netty.channel.ChannelHandler;

/**
 * @author nevermore on 2014/12/3
 */
public class MessageCodecFactory {

    public static ChannelHandler newEncoder() {
        return new MessageEncoder();
    }

    public static ChannelHandler newDecoder() {
        return new MessageDecoder();
    }
}
