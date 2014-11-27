package com.shadow.entity;

import com.shadow.entity.annotation.AutoSave;
import com.shadow.util.codec.JsonUtil;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

/**
 * @author nevermore on 2014/11/27
 */
@Entity
public class User extends CachedLifecycleEntity<Integer> {

    @Id
    private int id;
    private String username;
    private String password;
    private Date date;

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return JsonUtil.toJson(this);
    }

    @AutoSave
    public void updateUsername(String username) {
        setUsername(username);
    }
}
