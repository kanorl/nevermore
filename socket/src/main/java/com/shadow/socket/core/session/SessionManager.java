package com.shadow.socket.core.session;

import java.util.Collection;
import java.util.Optional;

/**
 * @author nevermore on 2015/1/12
 */
public interface SessionManager {

    Optional<Session> getSession(long identity);

    void bind(Session session, long identity);

    void write(long identity, short module, short cmd, Object data);

    void write(Collection<Long> identities, short module, short cmd, Object data);

    boolean isOnline(long identity);
}
