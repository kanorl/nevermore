package com.shadow.test.module.account.model;

import com.shadow.test.module.account.exception.AccountException;
import com.shadow.test.module.account.exception.AccountExceptionCode;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;

/**
 * @author nevermore on 2015/3/1
 */
public class AccountInfo {
    private String name;
    private short platform;
    private short server;

    public AccountInfo(String name, short platform, short server) {
        this.name = name;
        this.platform = platform;
        this.server = server;
    }

    public static AccountInfo forName(@Nonnull String accountName) {
        try {
            String[] arr = StringUtils.split(accountName, '_');
            return new AccountInfo(accountName, Short.parseShort(arr[1]), Short.parseShort(arr[2]));
        } catch (Exception e) {
            throw new AccountException(AccountExceptionCode.INVALID_ACCOUNT_NAME);
        }
    }

    public String getName() {
        return name;
    }

    public short getPlatform() {
        return platform;
    }

    public short getServer() {
        return server;
    }
}
