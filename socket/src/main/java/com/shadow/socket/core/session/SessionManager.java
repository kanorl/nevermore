package com.shadow.socket.core.session;

import com.shadow.socket.core.domain.Command;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * @author nevermore on 2015/1/12
 */
public interface SessionManager {

    Optional<Session> getSession(long identity);

    void bind(Session session, long identity);

    void send(long target, Command command, Object data);

    void send(Collection<Long> targets, Command command, Object data);

    void sendAll(Command command, Object data);

    void sendOnly(Command command, Object data, Predicate<Long> predicate);

    boolean isOnline(long identity);
}
