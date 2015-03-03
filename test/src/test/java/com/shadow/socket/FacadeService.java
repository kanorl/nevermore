package com.shadow.socket;

import com.shadow.socket.core.annotation.HandlerMethod;
import com.shadow.socket.core.annotation.RequestHandler;
import com.shadow.socket.core.annotation.RequestParam;

/**
 * @author nevermore on 2015/2/13
 */
@RequestHandler(module = 9998)
public interface FacadeService {

    @HandlerMethod(cmd = 1)
    int add(@RequestParam("a") int a, @RequestParam("b") int b);

}
