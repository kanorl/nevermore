package com.shadow.socket.netty.codec;

import com.shadow.socket.core.domain.Command;
import com.shadow.socket.core.domain.Message;
import com.shadow.socket.core.domain.Response;
import com.shadow.socket.core.domain.Result;
import com.shadow.util.codec.ProtostuffCodec;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * @author nevermore on 2014/11/26
 */
public class Response2MessageEncoder extends MessageToMessageEncoder<Response> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Response msg, List<Object> out) throws Exception {
        Result<?> result = msg.getResult();
        byte[] data = ProtostuffCodec.encode(result);

        Command command = msg.getCommand();
        Message message = Message.valueOf(command, data);
        out.add(message);
    }
}
