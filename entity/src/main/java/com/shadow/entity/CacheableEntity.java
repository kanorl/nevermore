package com.shadow.entity;


import com.shadow.entity.cache.annotation.Cacheable;
import org.hibernate.CallbackException;
import org.hibernate.Session;
import org.hibernate.classic.Lifecycle;

import java.io.Serializable;

/**
 * 可缓存的实体类
 *
 * @author nevermore on 2014/11/26.
 */
@Cacheable
public abstract class CacheableEntity<K extends Serializable> implements IEntity<K>, Lifecycle {
    @Override
    public final boolean onSave(Session session) throws CallbackException {
        prePersist();
        return false;
    }

    @Override
    public final boolean onUpdate(Session session) throws CallbackException {
        prePersist();
        return false;
    }

    @Override
    public final boolean onDelete(Session session) throws CallbackException {
        return false;
    }

    @Override
    public final void onLoad(Session session, Serializable serializable) {
        postLoad();
    }

    public void postLoad() {

    }

    public void prePersist() {

    }
}
