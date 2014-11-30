package com.shadow.socket.client.model;

/**
 * Created by LiangZengle on 2014/7/21.
 */
public class Param {
    private String key;
    private String value;
    private Class<?> type;

    public static Param valueOf(Object key, Object value, Class<?> type) {
        Param param = new Param();
        if (key != null) {
            param.key = key.toString().trim();
        }
        if (value != null) {
            param.value = value.toString().trim();
        }
        param.type = type;
        return param;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }
}
