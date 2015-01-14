package com.shadow.socket.core.session;

/**
 * @author nevermore on 2015/1/12
 */
public interface SessionManager {

    void bind(Session session, Object identity);

    void write(Object identity, Object data);

    boolean isOnline(Object identity);
}
