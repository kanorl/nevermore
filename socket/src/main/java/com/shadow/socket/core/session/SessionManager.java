package com.shadow.socket.core.session;

import java.util.Optional;

/**
 * @author nevermore on 2015/1/12
 */
public interface SessionManager {

    Optional<Session> getSession(Object identity);

    void bind(Session session, Object identity);

    void write(Object identity, Object data);

    boolean isOnline(Object identity);
}
