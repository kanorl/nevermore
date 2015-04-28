package com.shadow.test.module.account.facade;

import com.shadow.socket.core.annotation.HandlerMethod;
import com.shadow.socket.core.annotation.IdentityRequired;
import com.shadow.socket.core.annotation.RequestHandler;
import com.shadow.socket.core.annotation.RequestParam;
import com.shadow.socket.core.session.Session;
import com.shadow.test.module.player.model.Country;
import com.shadow.test.module.player.model.Gender;

/**
 * @author nevermore on 2015/3/1
 */
@IdentityRequired(false)
@RequestHandler(module = 1)
public interface AccountFacadeService {

    @HandlerMethod(cmd = 1)
    int create(@RequestParam("accountName") String accountName, @RequestParam("playerName") String playerName,
               @RequestParam("gender") Gender gender, @RequestParam("country") Country country);

    @HandlerMethod(cmd = 2)
    int login(Session session, @RequestParam("accountName") String accountName);
}
