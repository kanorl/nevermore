package com.shadow.socket.client.socket.codec;

import com.shadow.socket.core.domain.Message;
import com.shadow.socket.core.domain.Request;
import com.shadow.util.codec.ProtostuffCodec;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * Created by LiangZengle on 2014/9/15.
 */
public class Request2MessageEncoder extends MessageToMessageEncoder<Request> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Request msg, List<Object> out) throws Exception {
        byte[] data = ProtostuffCodec.encode(msg.getBody());
        Message message = Message.valueOf(msg.getCommand(), data);
        out.add(message);
    }
}
