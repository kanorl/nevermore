package com.shadow.socket.netty.session;

import com.shadow.socket.core.session.Session;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author nevermore on 2014/11/26
 */
public final class NettySessionManager {
    private static final AtomicLong ID_GENERATOR = new AtomicLong();
    private static final ConcurrentMap<Long, NettySession> SESSION_MAP = new ConcurrentHashMap<>();
    private static final AttributeKey<Session<Long>> SESSION_ATTRIBUTE_KEY = AttributeKey.valueOf("session");

    public static void bindSession(Channel channel) {
        NettySession session = NettySession.valueOf(nextId(), channel);
        while (SESSION_MAP.putIfAbsent(session.getId(), session) != null) {
            session = NettySession.valueOf(nextId(), channel);
        }
        channel.attr(SESSION_ATTRIBUTE_KEY).setIfAbsent(session);
    }


    public static void unbindSession(Channel channel) {
        channel.attr(SESSION_ATTRIBUTE_KEY).remove();
    }

    public static Session<Long> getSession(Channel channel) {
        return channel.attr(SESSION_ATTRIBUTE_KEY).get();
    }

    private static final int MAX_ID_LENGTH = String.valueOf(Long.MAX_VALUE).length();
    private static final int SUFFIX_MAX = 2 << 12; // 8192
    private static final char APPENDER = '0';

    private static long nextId() {
        String prefix = String.valueOf(System.currentTimeMillis());
        String suffix = String.valueOf(next() & (SUFFIX_MAX - 1));// mod SUFFIX_MAX
        String delimiter = StringUtils.repeat(APPENDER, MAX_ID_LENGTH - prefix.length() - suffix.length());
        return Long.parseLong(prefix + delimiter + suffix);
    }

    private static long next() {
        long next;
        do {
            next = ID_GENERATOR.incrementAndGet() & Long.MAX_VALUE;
        } while (next == 0);// when ID_GENERATOR.get() == Long.MIN_VALUE
        return next;
    }
}
