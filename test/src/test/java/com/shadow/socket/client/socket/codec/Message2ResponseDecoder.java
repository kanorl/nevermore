package com.shadow.socket.client.socket.codec;

import com.shadow.socket.core.domain.Message;
import com.shadow.socket.core.domain.Response;
import com.shadow.socket.core.domain.Result;
import com.shadow.util.codec.ProtostuffCodec;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * Created by LiangZengle on 2014/9/15.
 */
public class Message2ResponseDecoder extends MessageToMessageDecoder<Message> {

    @Override
    protected void decode(ChannelHandlerContext ctx, Message msg, List<Object> out) throws Exception {
        Result<?> result = new Result<>();
        ProtostuffCodec.decode(msg.getData(), result);

        Response response = Response.valueOf(msg.getCommand(), result);
        out.add(response);
    }
}
