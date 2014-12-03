package com.shadow.socket.netty.server.handler;

import com.shadow.socket.core.annotation.support.RequestProcessor;
import com.shadow.socket.core.annotation.support.RequestProcessorManager;
import com.shadow.socket.core.domain.Request;
import com.shadow.socket.core.domain.Response;
import com.shadow.socket.core.domain.Result;
import com.shadow.socket.core.session.Session;
import com.shadow.socket.netty.session.NettySessionManager;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author nevermore on 2014/11/26
 */
@Component
@ChannelHandler.Sharable
public class ServerHandler extends SimpleChannelInboundHandler<Request> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Request request) throws Exception {
        Session<Long> session = NettySessionManager.getSession(ctx.channel());
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
        session.write(response);
    }
}
