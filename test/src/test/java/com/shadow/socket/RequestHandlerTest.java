package com.shadow.socket;

import com.shadow.socket.core.annotation.HandlerMethod;
import com.shadow.socket.core.annotation.RequestHandler;
import org.springframework.stereotype.Component;

/**
 * @author nevermore on 2014/11/30.
 */
@RequestHandler(module = 1)
@Component
public class RequestHandlerTest {

    @HandlerMethod(cmd = 0)
    public int sum(int a, int b) {
        return a + b;
    }
}
