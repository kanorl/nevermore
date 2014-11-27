package com.shadow.net.socket.netty.server.handler;

import com.shadow.net.socket.core.annotation.support.RequestProcessor;
import com.shadow.net.socket.core.annotation.support.RequestProcessorManager;
import com.shadow.net.socket.core.domain.AttrKey;
import com.shadow.net.socket.core.domain.Request;
import com.shadow.net.socket.core.domain.Response;
import com.shadow.net.socket.core.domain.Result;
import com.shadow.net.socket.core.session.Session;
import com.shadow.net.socket.netty.session.NettySessionManager;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author nevermore on 2014/11/26
 */
@ChannelHandler.Sharable
public class ServerHandler extends SimpleChannelInboundHandler<Request> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerHandler.class);
    private static final AttributeKey<Session> SESSION_ATTRIBUTE_KEY = AttributeKey.valueOf("session");


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Session session = NettySessionManager.uniqueSession(ctx.channel());
        ctx.attr(SESSION_ATTRIBUTE_KEY).setIfAbsent(session);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Request request) throws Exception {
        Session<Long> session = NettySessionManager.uniqueSession(ctx.channel());
        session.setAttr(AttrKey.IDENTITY, 1L);
        request.setSession(session);

        RequestProcessor requestProcessor = RequestProcessorManager.getRequestProcessor(request.getCommand());

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

        Result<?> result = Result.valueOf(code, content);
        Response response = Response.valueOf(request.getCommand(), result);
        request.getSession().write(response);
    }
}
