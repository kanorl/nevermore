package com.shadow.socket.netty.server.handler;

import com.shadow.socket.core.annotation.support.RequestProcessor;
import com.shadow.socket.core.annotation.support.RequestProcessorManager;
import com.shadow.socket.core.domain.Message;
import com.shadow.socket.core.domain.ParameterContainer;
import com.shadow.socket.core.domain.Request;
import com.shadow.socket.core.domain.Result;
import com.shadow.socket.core.session.Session;
import com.shadow.socket.netty.server.session.ServerSessionHandler;
import com.shadow.util.codec.Codec;
import com.shadow.util.exception.CheckedException;
import com.shadow.util.exception.CheckedExceptionCode;
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
    @Autowired
    private Codec codec;

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        Request request = message2Request(msg, sessionHandler.getSession(ctx.channel()));

        RequestProcessor requestProcessor = requestProcessorManager.getProcessor(request);

        Object result = null;
        int code = CheckedExceptionCode.SUCCESS;
        try {
            result = requestProcessor.handle(request);
        } catch (Exception e) {
            if (e.getCause() instanceof CheckedException) {
                code = ((CheckedException) e.getCause()).code();
            } else {
                code = CheckedExceptionCode.UNKNOWN;
                LOGGER.error("处理请求发生异常: " + request.getCommand(), e);
            }
        }

        if (requestProcessor.isOmitResponse()) {
            return;
        }

        Message respMsg = Message.valueOf(request.getCommand(), codec.encode(Result.valueOf(code, result)));
        request.getSession().send(respMsg);
    }

    private Request message2Request(Message msg, Session session) {
        ParameterContainer pc = codec.decode(msg.getBody(), ParameterContainer.class);
        return Request.valueOf(msg.getCommand(), pc, session);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error(cause.getMessage(), cause);
    }
}
