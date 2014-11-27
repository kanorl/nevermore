package com.shadow.net.socket.netty.codec;

import com.shadow.net.socket.core.domain.Command;
import com.shadow.net.socket.core.domain.Message;
import com.shadow.net.socket.core.domain.Response;
import com.shadow.net.socket.core.domain.Result;
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
