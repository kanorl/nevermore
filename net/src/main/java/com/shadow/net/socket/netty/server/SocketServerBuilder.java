package com.shadow.net.socket.netty.server;

import java.util.concurrent.ThreadFactory;

/**
 * @author nevermore on 2014/11/26
 */
public final class SocketServerBuilder {
    public static final int DEFAULT_PARENT_EVENT_LOOP_THREADS = 1;
    public static final int DEFAULT_CHILD_EVENT_LOOP_THREADS = Runtime.getRuntime().availableProcessors() * 2;
    private int port;
    private int parentThreads;
    private int childThreads;
    private ThreadFactory threadFactory;


    public static SocketServerBuilder newBuilder() {
        return new SocketServerBuilder();
    }

    public SocketServerBuilder port(int port) {
        // TODO assert > 0
        this.port = port;
        return this;
    }

    public SocketServerBuilder parentThreads(int n) {
        this.parentThreads = n;
        return this;
    }

    public SocketServerBuilder childThreads(int n) {
        this.childThreads = n;
        return this;
    }

    public SocketServerBuilder threadFactory(ThreadFactory factory) {
        this.threadFactory = factory;
        return this;
    }

    public SocketServer build() {
        return SocketServer.create(this);
    }

    public int getPort() {
        return port;
    }

    public int getParentThreads() {
        return Math.max(parentThreads, DEFAULT_PARENT_EVENT_LOOP_THREADS);
    }

    public int getChildThreads() {
        return Math.max(childThreads, DEFAULT_CHILD_EVENT_LOOP_THREADS);
    }

    public ThreadFactory getThreadFactory() {
        return threadFactory;
    }
}
