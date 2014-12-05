package com.shadow.entity;

import org.hibernate.CallbackException;
import org.hibernate.Session;
import org.hibernate.classic.Lifecycle;

import java.io.Serializable;

/**
 * 实现Hibernate Lifecycle的可缓存实体类
 *
 * @author nevermore on 2014/11/26.
 */
public abstract class CacheableLifecycleEntity<K extends Serializable> extends CacheableEntity<K> implements Lifecycle {
    @Override
    public boolean onSave(Session session) throws CallbackException {
        return false;
    }

    @Override
    public boolean onUpdate(Session session) throws CallbackException {
        return false;
    }

    @Override
    public boolean onDelete(Session session) throws CallbackException {
        return false;
    }

    @Override
    public void onLoad(Session session, Serializable serializable) {

    }
}
