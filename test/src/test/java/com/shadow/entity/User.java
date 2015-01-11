package com.shadow.entity;

import com.shadow.entity.annotation.AutoSave;
import com.shadow.entity.annotation.CacheSize;
import com.shadow.entity.annotation.Cacheable;
import com.shadow.entity.annotation.PreLoaded;
import com.shadow.util.codec.JsonUtil;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import java.util.Date;

/**
 * @author nevermore on 2014/11/27
 */
@Entity
@Cacheable(cacheSize = @CacheSize(factor = 2))
@PreLoaded(policy = PreLoaded.Policy.NAMED_QUERY)
@NamedQueries(@NamedQuery(name = "User.init", query = "From User where date >= curdate() "))
public class User extends CacheableLifecycleEntity<Integer> {

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
    public void postLoad() {

    }

    @Override
    public void prePersist() {

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
