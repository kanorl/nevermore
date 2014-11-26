package com.shadow.entity.orm;

import com.shadow.entity.IEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

/**
 * 数据访问器Hibernate实现
 *
 * @author nevermore on 2014/11/26.
 */
@SuppressWarnings("unchecked")
@Repository
@Transactional
public final class HibernateDataAccessor implements DataAccessor {

    @Autowired
    private SessionFactory sessionFactory;

    private Session currentSession() {
        return sessionFactory.getCurrentSession();
    }

    @Transactional(readOnly = true)
    @Override
    public <K extends Serializable, V extends IEntity<K>> V get(K id, Class<V> clazz) {
        return (V) currentSession().get(clazz, id);
    }

    @Override
    public <K extends Serializable, V extends IEntity<K>> K save(V v) {
        return (K) currentSession().save(v);
    }

    @Override
    public <K extends Serializable, V extends IEntity<K>> void update(V v) {
        currentSession().update(v);
    }

    @Override
    public <K extends Serializable, V extends IEntity<K>> void delete(V v) {
        currentSession().delete(v);
    }
}
