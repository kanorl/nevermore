package com.shadow.socket.netty.server.handler;

import com.shadow.socket.core.annotation.support.RequestProcessor;
import com.shadow.socket.core.annotation.support.RequestProcessorManager;
import com.shadow.socket.core.domain.*;
import com.shadow.socket.core.session.Session;
import com.shadow.socket.netty.server.session.ServerSessionHandler;
import com.shadow.util.codec.ProtostuffCodec;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author nevermore on 2014/11/26
 */
@Component
@ChannelHandler.Sharable
public class ServerHandler extends SimpleChannelInboundHandler<Message> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerHandler.class);

    @Autowired
    private ServerSessionHandler sessionHandler;
    @Autowired
    private RequestProcessorManager requestProcessorManager;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        Request request = toRequest(msg, sessionHandler.getSession(ctx.channel()));

        RequestProcessor requestProcessor = requestProcessorManager.getProcessor(request);

        Object content = null;
        int code = 0;
        try {
            content = requestProcessor.handle(request);
        } catch (IllegalArgumentException e) {
            code = -2;
            LOGGER.error(e.getMessage(), e);
        } catch (Exception e) {
            code = -1;
            LOGGER.error(e.getMessage(), e);
        }

        if (requestProcessor.isOmitResponse()) {
            return;
        }

        Message response = toResponseMsg(request.getCommand(), code, content);
        request.getSession().write(response);
    }

    private Message toResponseMsg(Command command, int code, Object content) {
        Result<?> result = Result.valueOf(code, content);
        byte[] body = ProtostuffCodec.encode(result);
        return Message.valueOf(command, body);
    }

    private Request toRequest(Message msg, Session session) {
        ParameterContainer pc = new ParameterContainer();
        ProtostuffCodec.decode(msg.getBody(), pc);
        return Request.valueOf(msg.getCommand(), pc, session);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error(cause.getMessage(), cause);
    }
}
