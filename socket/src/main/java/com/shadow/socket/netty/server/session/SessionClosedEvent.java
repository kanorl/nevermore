package com.shadow.socket.netty.server.session;

import com.shadow.event.Event;
import com.shadow.socket.core.session.Session;

import java.util.Optional;

/**
 * @author nevermore on 2015/3/4
 */
public class SessionClosedEvent implements Event {

    private final Session session;
    private final Optional<Long> identity;

    public SessionClosedEvent(Session session, Optional<Long> identity) {
        this.session = session;
        this.identity = identity;
    }

    public static SessionClosedEvent valueOf(Session session, Optional<Long> identity) {
        return new SessionClosedEvent(session, identity);
    }

    public Session getSession() {
        return session;
    }

    public Optional<Long> getIdentity() {
        return identity;
    }
}
