package com.shadow.event;

import com.shadow.socket.core.annotation.RequestHandler;
import com.shadow.test.module.account.facade.AccountFacadeServiceImpl;
import com.shadow.util.lang.ReflectUtil;

/**
 * @author nevermore on 2014/12/28.
 */
public class MyEvent implements Event {
    private String name;

    public MyEvent(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static void main(String[] args) {
        RequestHandler requestHandler = ReflectUtil.getDeclaredAnnotation(AccountFacadeServiceImpl.class,
                RequestHandler.class);
        System.out.println(requestHandler == null);
    }
}
