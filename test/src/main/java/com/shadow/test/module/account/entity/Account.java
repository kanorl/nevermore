package com.shadow.test.module.account.entity;

import com.shadow.entity.CacheableEntity;
import com.shadow.entity.cache.annotation.AutoSave;

import javax.persistence.*;
import java.util.Date;

/**
 * @author nevermore on 2015/3/1
 */
@Entity
@Table(indexes = @Index(name = "Account.name", columnList = "name", unique = true))
@NamedQueries(@NamedQuery(name = Account.QUERY_NAME_AND_ID, query = "select name, id from Account"))
public class Account extends CacheableEntity<Long> {

    public static final String QUERY_NAME_AND_ID = "Account.name2Id";

    @Id
    private Long id;
    @Column(unique = true)
    private String name;
    private Date registerTime;
    private Date loginTime;
    private Date logoutTime;


    public static Account valueOf(Long id, String name) {
        Account account = new Account();
        account.id = id;
        account.name = name;
        account.registerTime = new Date();
        return account;
    }

    @AutoSave
    public void onLogin() {
        loginTime = new Date();
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Date getRegisterTime() {
        return registerTime;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public Date getLogoutTime() {
        return logoutTime;
    }
}
