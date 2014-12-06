package com.shadow.entity.orm;

import com.shadow.entity.IEntity;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

/**
 * 数据访问器Hibernate实现
 *
 * @author nevermore on 2014/11/26.
 */
/*
 * 添加depends-on，解决异常：
 * org.springframework.beans.factory.BeanCreationNotAllowedException: Error creating bean with name 'transactionManager': Singleton bean creation not allowed while the singletons of this factory are in destruction (Do not request a bean from a BeanFactory in a destroy method implementation!)
 * 保证transactionManager销毁顺序在本类之后
 */
@DependsOn("transactionManager")
@SuppressWarnings("unchecked")
@Repository
@Transactional
public class HibernateDataAccessor implements DataAccessor {

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
    public <V extends IEntity<?>> void update(V v) {
        currentSession().update(v);
    }

    @Override
    public <V extends IEntity<?>> void delete(V v) {
        currentSession().delete(v);
    }

    @Transactional(readOnly = true)
    @Override
    public <V extends IEntity<?>> List<V> getAll(Class<V> clazz) {
        return currentSession().createCriteria(clazz).list();
    }

    @Transactional(readOnly = true)
    @Override
    public <V extends IEntity<?>> List<V> namedQuery(Class<V> clazz, String queryName, Object... queryParams) {
        Query query = currentSession().getNamedQuery(queryName);
        if (queryParams != null) {
            for (int i = 0; i < queryParams.length; i++) {
                Object queryParam = queryParams[i];
                query.setParameter(i, queryParam);
            }
        }
        return query.list();
    }
}
