package com.shadow.test.module.account.service;

import com.shadow.entity.lock.LockChain;
import com.shadow.entity.lock.annotation.AutoLocked;
import com.shadow.entity.lock.annotation.LockTarget;
import com.shadow.test.module.account.entity.Account;
import com.shadow.test.module.account.model.AccountInfo;
import com.shadow.test.module.player.entity.Player;
import com.shadow.test.module.player.model.Country;
import com.shadow.test.module.player.model.Gender;
import com.shadow.test.module.player.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author nevermore on 2015/3/1
 */
@Component
public class AccountService {
    @Autowired
    private AccountManager accountManager;
    @Autowired
    private PlayerService playerService;

    public void create(AccountInfo accountInfo, String playerName, Gender gender, Country country) {
        Account account = accountManager.getOrCreate(accountInfo);
        Player player = playerService.create(account.getId(), playerName, gender, country);
    }

    public Optional<Account> getAccountByName(String accountName) {
        return accountManager.getAccountByName(accountName);
    }

    @AutoLocked
    public void login(@LockTarget Account account) {
        boolean isLocked = LockChain.isLockedByCurrentThread(account);
        account.onLogin();
    }
}
