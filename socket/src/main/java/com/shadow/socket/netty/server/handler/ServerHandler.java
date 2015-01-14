package com.shadow.socket.netty.server.handler;

import com.shadow.socket.core.annotation.support.RequestProcessor;
import com.shadow.socket.core.annotation.support.RequestProcessorManager;
import com.shadow.socket.core.domain.*;
import com.shadow.socket.core.exception.CheckedException;
import com.shadow.socket.core.session.Session;
import com.shadow.socket.netty.server.session.ServerSessionHandler;
import com.shadow.util.codec.ProtostuffCodec;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author nevermore on 2014/11/26
 */
@Component
@ChannelHandler.Sharable
public class ServerHandler extends ChannelDuplexHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerHandler.class);

    @Autowired
    private ServerSessionHandler sessionHandler;
    @Autowired
    private RequestProcessorManager requestProcessorManager;

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof Response) {
            Response response = (Response) msg;
            byte[] body = ProtostuffCodec.encode(response.getResult());
            msg = Message.valueOf(response.getCommand(), body);
        }
        super.write(ctx, msg, promise);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Message message = (Message) msg;
        Request request = message2Request(message, sessionHandler.getSession(ctx.channel()));

        RequestProcessor requestProcessor = requestProcessorManager.getProcessor(request);

        Object content = null;
        int code = 0;
        try {
            content = requestProcessor.handle(request);
        } catch (CheckedException e) {
            code = e.getCode();
            LOGGER.error(e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (requestProcessor.isOmitResponse()) {
            return;
        }

        Response response = Response.valueOf(request.getCommand(), Result.valueOf(code, content));
        request.getSession().write(response);

        super.channelRead(ctx, msg);
    }

    private Request message2Request(Message msg, Session session) {
        ParameterContainer pc = new ParameterContainer();
        ProtostuffCodec.decode(msg.getBody(), pc);
        return Request.valueOf(msg.getCommand(), pc, session);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error(cause.getMessage(), cause);
    }
}
