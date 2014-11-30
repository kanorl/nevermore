package com.shadow.socket.core.domain;

import java.io.Serializable;

/**
 * @author nevermore on 2014/11/26.
 */
public final class Result<T> implements Serializable {
    private static final long serialVersionUID = -5654338120798899507L;
    private int code;
    private T content;

    public static <T> Result<T> valueOf(int code, T content) {
        Result<T> result = new Result<>();
        result.code = code;
        result.content = content;
        return result;
    }

    public static <T> Result<T> success(T t) {
        Result<T> result = new Result<>();
        result.content = t;
        return result;
    }

    public static <T> Result<T> error(int code) {
        Result<T> result = new Result<>();
        result.code = code;
        return result;
    }

    public int getCode() {
        return code;
    }

    public T getContent() {
        return content;
    }
}
