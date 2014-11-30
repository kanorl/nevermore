package com.shadow.socket.netty.codec;

import com.shadow.socket.core.domain.Command;
import com.shadow.socket.core.domain.Message;
import com.shadow.socket.core.domain.ParameterContainer;
import com.shadow.socket.core.domain.Request;
import com.shadow.util.codec.ProtostuffCodec;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * @author nevermore on 2014/11/26
 */
public class Message2RequestDecoder extends MessageToMessageDecoder<Message> {
    @Override
    protected void decode(ChannelHandlerContext ctx, Message msg, List<Object> out) throws Exception {
        if (msg == null) {
            return;
        }

        ParameterContainer pc = new ParameterContainer();
        ProtostuffCodec.decode(msg.getData(), pc);

        Command command = msg.getCommand();

        Request request = Request.valueOf(command, pc, null);
        out.add(request);
    }
}
