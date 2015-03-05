package com.shadow.socket.netty.server.session;

import com.shadow.event.EventBus;
import com.shadow.socket.core.domain.AttrKey;
import com.shadow.socket.core.session.Session;
import com.shadow.socket.core.session.SessionManager;
import com.shadow.util.codec.Codec;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author nevermore on 2014/11/30.
 */
@Component
@ChannelHandler.Sharable
public class ServerSessionHandler extends ChannelInboundHandlerAdapter implements SessionManager {

    @Autowired
    private EventBus eventBus;
    @Autowired
    private Codec codec;

    private final AtomicLong ID_GENERATOR = new AtomicLong();
    private final ConcurrentMap<Long, Session> SESSIONS = new ConcurrentHashMap<>();
    private final ConcurrentMap<Long, Session> IDENTIFIED_SESSIONS = new ConcurrentHashMap<>();
    private final AttributeKey<Session> SESSION_ATTRIBUTE_KEY = AttributeKey.valueOf("session");
    private static final int MAX_ID_LENGTH = String.valueOf(Long.MAX_VALUE).length();
    private static final int SUFFIX_MAX = 1 << 13; // 8192
    private static final char APPENDER = '0';

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        NettySession session = NettySession.valueOf(nextId(), channel);
        while (SESSIONS.putIfAbsent(session.getId(), session) != null) {
            session = NettySession.valueOf(nextId(), channel);
        }
        channel.attr(SESSION_ATTRIBUTE_KEY).setIfAbsent(session);
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Session session = ctx.channel().attr(SESSION_ATTRIBUTE_KEY).getAndRemove();
        if (session != null) {
            SESSIONS.remove(session.getId());
            session.getAttr(AttrKey.IDENTITY).ifPresent(IDENTIFIED_SESSIONS::remove);
            eventBus.post(SessionClosedEvent.valueOf(session, session.getAttr(AttrKey.IDENTITY)));
        }
        super.channelInactive(ctx);
    }

    private long nextId() {
        String prefix = String.valueOf(System.currentTimeMillis());
        String suffix = String.valueOf(next() & (SUFFIX_MAX - 1));// mod SUFFIX_MAX
        String delimiter = StringUtils.repeat(APPENDER, MAX_ID_LENGTH - prefix.length() - suffix.length());
        return Long.parseLong(prefix + delimiter + suffix);
    }

    private long next() {
        long next;
        do {
            next = ID_GENERATOR.incrementAndGet() & Long.MAX_VALUE;
        } while (next == 0);// when ID_GENERATOR.get() == Long.MIN_VALUE
        return next;
    }

    @Override
    public void bind(Session session, long identity) {
        if (session.setAttrIfAbsent(AttrKey.IDENTITY, identity).isPresent()) {
            throw new IllegalStateException("Session已被绑定：identity=" + session.getAttr(AttrKey.IDENTITY).get());
        }
        IDENTIFIED_SESSIONS.put(identity, session);
    }

    public Session getSession(Channel channel) {
        return channel.attr(SESSION_ATTRIBUTE_KEY).get();
    }

    @Override
    public void write(long identity, short module, short cmd, Object data) {
        getSession(identity).ifPresent(session -> session.write(data));
    }

    @Override
    public void write(Collection<Long> identities, short module, short cmd, Object data) {
        ByteBuf buf = Unpooled.copiedBuffer(codec.encode(data));
    }

    @Override
    public boolean isOnline(long identity) {
        return getSession(identity).isPresent();
    }

    @Override
    public Optional<Session> getSession(long identity) {
        return Optional.ofNullable(IDENTIFIED_SESSIONS.get(identity));
    }
}
