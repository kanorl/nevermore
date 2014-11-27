package com.shadow.net.socket.core.domain;


import com.shadow.net.socket.core.session.Session;
import com.shadow.util.codec.JsonUtil;

/**
 * @author nevermore on 2014/11/26.
 */
public final class Request {
    private Command command;
    private ParameterContainer body;
    private Session<Long> session;

    public static Request valueOf(Command command, ParameterContainer body, Session<Long> session) {
        Request request = new Request();
        request.command = command;
        request.body = body;
        request.session = session;
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

    public void setSession(Session<Long> session) {
        this.session = session;
    }
}
