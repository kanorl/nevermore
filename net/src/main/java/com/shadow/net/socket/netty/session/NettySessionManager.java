package com.shadow.net.socket.netty.session;

import io.netty.channel.Channel;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author nevermore on 2014/11/26
 */
public  final  class NettySessionManager {
    private static final AtomicLong ID_GENERATOR = new AtomicLong();
    private static final ConcurrentMap<Long, NettySession> SESSION_MAP = new ConcurrentHashMap<>();

    public static NettySession uniqueSession(Channel channel) {
        NettySession nettySession = NettySession.valueOf(nextId(), channel);
        while (SESSION_MAP.putIfAbsent(nettySession.getId(), nettySession) != null) {
            nettySession = NettySession.valueOf(nextId(), channel);
        }
        return nettySession;
    }

    private static final int maxIdLength = String.valueOf(Long.MAX_VALUE).length();
    private static final char SEPARATOR = '0';

    private static long nextId() {
        StringBuilder sb = new StringBuilder();

        long ms = System.currentTimeMillis();
        sb.append(ms);

        for (int i = 0; i <= maxIdLength - sb.length() - 4; i++) {
            sb.append(SEPARATOR);
        }

        long suffix = ID_GENERATOR.incrementAndGet();
        suffix = suffix & (1024 - 1);
        sb.append(suffix);

        return Long.parseLong(sb.toString());
    }

    public static void main(String[] args) {
        for (int i = 0; i < 1025; i++) {
            System.out.println(nextId());
        }
    }
}
