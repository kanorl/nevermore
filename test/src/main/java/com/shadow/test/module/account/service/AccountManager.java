package com.shadow.test.module.account.service;

import com.shadow.entity.cache.EntityCache;
import com.shadow.entity.id.EntityIdGenerator;
import com.shadow.entity.orm.DataAccessor;
import com.shadow.test.module.account.entity.Account;
import com.shadow.test.module.account.exception.AccountException;
import com.shadow.test.module.account.exception.AccountExceptionCode;
import com.shadow.test.module.account.model.AccountInfo;
import com.shadow.util.exception.OperationFailedException;
import com.shadow.util.injection.Injected;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author nevermore on 2015/3/1
 */
@Component
public class AccountManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountManager.class);

    @Autowired
    private DataAccessor dataAccessor;
    @Autowired
    private EntityIdGenerator idGenerator;
    @Injected
    private EntityCache<Long, Account> accountCache;

    private ConcurrentMap<String, Long> name2Id;

    @PostConstruct
    private void init() {
        List<Object[]> result = dataAccessor.namedQuery(Account.QUERY_NAME_AND_ID);
        name2Id = new ConcurrentHashMap<>(result.size());
        result.forEach(e -> name2Id.put((String) e[0], (Long) e[1]));
    }

    Optional<Account> getAccountByName(String name) {
        Long id = name2Id.get(name);
        if (id == null) {
            return Optional.empty();
        }
        return accountCache.get(id);
    }

    Account getOrCreate(AccountInfo accountInfo) {
        final String name = accountInfo.getName();
        Optional<Account> accountOptional = getAccountByName(name);
        if (accountOptional.isPresent()) {
            return accountOptional.get();
        }

        if (name2Id.putIfAbsent(name, 0L) != null) {
            throw new AccountException(AccountExceptionCode.ACCOUNT_NAME_EXISTS);
        }

        try {
            Long id = idGenerator.next(Account.class, accountInfo.getPlatform(), accountInfo.getServer());
            Account account = accountCache.getOrCreate(id, () -> Account.valueOf(id, name));
            name2Id.replace(name, 0L, account.getId());
            return account;
        } catch (Exception e) {
            name2Id.remove(name, 0L);
            LOGGER.error("创建账号失败", e);
            throw new OperationFailedException();
        }
    }
}
