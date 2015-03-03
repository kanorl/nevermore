package com.shadow.test.module.account.facade;

import com.shadow.event.EventBus;
import com.shadow.socket.core.session.Session;
import com.shadow.socket.core.session.SessionManager;
import com.shadow.test.module.account.entity.Account;
import com.shadow.test.module.account.exception.AccountExceptionCode;
import com.shadow.test.module.account.exception.AccountNotExistsException;
import com.shadow.test.module.account.exception.IllegalPlatformException;
import com.shadow.test.module.account.exception.IllegalServerException;
import com.shadow.test.module.account.model.AccountInfo;
import com.shadow.test.module.account.service.AccountService;
import com.shadow.test.module.player.entity.Player;
import com.shadow.test.module.player.event.PlayerLoginEvent;
import com.shadow.test.module.player.exception.PlayerNotExistsException;
import com.shadow.test.module.player.model.Country;
import com.shadow.test.module.player.model.Gender;
import com.shadow.test.module.player.service.PlayerService;
import com.shadow.util.config.ServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author nevermore on 2015/3/1
 */
@Component
public class AccountFacadeServiceImpl implements AccountFacadeService {

    @Autowired
    private ServerConfig serverConfig;
    @Autowired
    private AccountService accountService;
    @Autowired
    private PlayerService playerService;
    @Autowired
    private SessionManager sessionManager;
    @Autowired
    private EventBus eventBus;

    @Override
    public int create(String accountName, String playerName, Gender gender, Country country) {
        AccountInfo accountInfo = AccountInfo.forName(accountName);

        checkPlatformAndServer(accountInfo.getPlatform(), accountInfo.getServer());

        accountService.create(accountInfo, playerName, gender, country);

        return AccountExceptionCode.SUCCESS;
    }

    @Override
    public int login(Session session, String accountName) {
        AccountInfo accountInfo = AccountInfo.forName(accountName);

        checkPlatformAndServer(accountInfo.getPlatform(), accountInfo.getServer());

        Account account = accountService.getAccountByName(accountName).orElseThrow(AccountNotExistsException::new);
        Player player = playerService.getPlayer(account.getId()).orElseThrow(PlayerNotExistsException::new);

        sessionManager.getSession(account.getId()).ifPresent(s -> {
            // todo sync logout
        });

        sessionManager.bind(session, account.getId());

        accountService.login(account);
        eventBus.post(PlayerLoginEvent.valueOf(player));

        return AccountExceptionCode.SUCCESS;
    }

    private void checkPlatformAndServer(short platform, short server) {
        if (!serverConfig.getPlatforms().contains(platform)) {
            throw new IllegalPlatformException();
        }
        if (!serverConfig.getServers(platform).contains(server)) {
            throw new IllegalServerException();
        }
    }
}
