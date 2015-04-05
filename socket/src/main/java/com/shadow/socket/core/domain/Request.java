package com.shadow.socket.core.domain;


import com.shadow.common.util.codec.JsonUtil;
import com.shadow.socket.core.session.Session;

/**
 * @author nevermore on 2014/11/26.
 */
public final class Request {
    private Command command;
    private ParameterContainer body;
    private Session session;

    public static Request valueOf(Command command, ParameterContainer body, Session session) {
        Request request = new Request();
        request.command = command;
        request.body = body;
        request.session = session;
        return request;
    }

    public static Request valueOf(Command command, ParameterContainer body) {
        Request request = new Request();
        request.command = command;
        request.body = body;
        return request;
    }

    @Override
    public String toString() {
        return JsonUtil.toJson(this);
    }

    public Command getCommand() {
        return command;
    }

    public ParameterContainer getBody() {
        return body;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
