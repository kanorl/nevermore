package com.shadow.test.module.account.service;

import com.mongodb.client.model.Projections;
import com.shadow.common.exception.OperationFailedException;
import com.shadow.common.injection.Injected;
import com.shadow.entity.cache.EntityCache;
import com.shadow.entity.db.mongo.MongoDataStore;
import com.shadow.entity.id.EntityIdGenerator;
import com.shadow.test.module.account.entity.Account;
import com.shadow.test.module.account.exception.AccountException;
import com.shadow.test.module.account.exception.AccountExceptionCode;
import com.shadow.test.module.account.model.AccountInfo;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

/**
 * @author nevermore on 2015/3/1
 */
@Component
public class AccountManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountManager.class);

    @Autowired
    private MongoDataStore ds;
    @Autowired
    private EntityIdGenerator idGenerator;
    @Injected
    private EntityCache<Long, Account> accountCache;

    private ConcurrentMap<String, Long> name2Id = new ConcurrentHashMap<>();

    @PostConstruct
    private void init() {
        ds.getMongoCollection(Account.class).find().projection(Projections.include("_id", "name")).forEach((Consumer<Document>) document -> {
            Long prev = name2Id.put(document.get("name", String.class), document.get("_id", Long.class));
            assert prev == null;
        });
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
