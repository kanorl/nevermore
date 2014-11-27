package com.shadow.net.socket.core.domain;

/**
 * @author nevermore on 2014/11/26.
 */
public final class Response {
    private Command command;
    private Result result;

    public static Response valueOf(Command command, Result<?> result) {
        Response response = new Response();
        response.command = command;
        response.result = result;
        return response;
    }

    public Command getCommand() {
        return command;
    }

    public Result getResult() {
        return result;
    }
}
