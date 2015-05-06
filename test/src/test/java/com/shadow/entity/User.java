package com.shadow.entity;

import com.shadow.common.util.codec.JsonUtil;
import com.shadow.entity.cache.annotation.AutoSave;
import com.shadow.entity.cache.annotation.CacheSize;
import com.shadow.entity.cache.annotation.Cacheable;
import com.shadow.entity.cache.annotation.PreLoaded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.Date;

/**
 * @author nevermore on 2014/11/27
 */
@Entity
@Cacheable(cacheSize = @CacheSize(factor = 2))
@PreLoaded(policy = PreLoaded.Policy.ALL)
public class User implements IEntity<Integer> {

    @Id
    private int id;
    private String username;
    private String password;
    private Date date;

    public static User valueOf(int id) {
        User user = new User();
        user.setId(id);
        return user;
    }

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
