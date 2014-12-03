package com.shadow.socket;

import com.shadow.entity.User;
import com.shadow.entity.UserDto;
import com.shadow.entity.UserService;
import com.shadow.socket.core.annotation.HandlerMethod;
import com.shadow.socket.core.annotation.RequestHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author nevermore on 2014/11/30.
 */
@RequestHandler(module = 1)
@Component
public class RequestHandlerTest {

    @Autowired
    private UserService userService;

    @HandlerMethod(cmd = 0)
    public int sum(int a, int b) {
        return a + b;
    }

    @HandlerMethod(cmd = 1)
    public String getUsername(int id) {
        User user = userService.getUser(id);
        return user.getUsername();
    }

    @HandlerMethod(cmd = 2)
    public UserDto getUser(int id) {
        return UserDto.valueOf(userService.getUser(id));
    }
}
