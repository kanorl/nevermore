package com.shadow.entity.orm;

import com.shadow.entity.IEntity;
import com.shadow.entity.id.Range;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.unmodifiableList;
import static org.apache.commons.collections4.ListUtils.emptyIfNull;

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
@Transactional(readOnly = true)
public class HibernateDataAccessor implements DataAccessor {

    @Autowired
    private SessionFactory sessionFactory;

    private Session currentSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public <K extends Serializable, V extends IEntity<K>> V get(@Nonnull K id, @Nonnull Class<V> clazz) {
        return (V) currentSession().get(clazz, id);
    }

    @Transactional(readOnly = false)
    @Override
    public <K extends Serializable, V extends IEntity<K>> K save(@Nonnull V v) {
        return (K) currentSession().save(v);
    }

    @Transactional(readOnly = false)
    @Override
    public <V extends IEntity<?>> void update(@Nonnull V v) {
        currentSession().update(v);
    }

    @Transactional(readOnly = false)
    @Override
    public <V extends IEntity<?>> void delete(@Nonnull V v) {
        currentSession().delete(v);
    }

    @Transactional(readOnly = false)
    @Override
    public <V extends IEntity<?>> void saveOrUpdate(@Nonnull V v) {
        currentSession().saveOrUpdate(v);
    }

    @Override
    public <V extends IEntity<?>> List<V> namedQuery(@Nonnull Class<V> clazz, @Nonnull String queryName, @Nullable Object... queryParams) {
        Query query = currentSession().getNamedQuery(queryName);
        if (queryParams != null) {
            for (int i = 0; i < queryParams.length; i++) {
                Object queryParam = queryParams[i];
                query.setParameter(i, queryParam);
            }
        }
        return unmodifiableList(emptyIfNull(query.list()));
    }

    @Override
    public <V extends IEntity<?>> List<V> query(@Nonnull Class<V> clazz) {
        return query(clazz, null);
    }

    @Nonnull
    @Override
    public <V extends IEntity<?>, T> List<T> query(@Nonnull Class<V> clazz, @Nullable Map<String, ?> propertyNameValues, @Nullable Projection... projections) {
        Criteria c = currentSession().createCriteria(clazz);
        if (propertyNameValues != null) {
            c.add(Restrictions.allEq(propertyNameValues));
        }
        if (projections != null) {
            for (Projection projection : projections) {
                c.setProjection(projection);
            }
        }
        return unmodifiableList(emptyIfNull(c.list()));
    }

    @Nonnull
    @Override
    public <K extends Serializable, V extends IEntity<K>> List<K> queryIds(@Nonnull Class<V> clazz, @Nonnull Map<String, Object> stringObjectMap) {
        return query(clazz, stringObjectMap, Projections.id());
    }

    @Override
    public <K extends Long, V extends IEntity<K>> Optional<K> queryMaxId(@Nonnull Class<V> clazz, Range range) {
        String pName = sessionFactory.getClassMetadata(clazz).getIdentifierPropertyName();
        return Optional.ofNullable((K) currentSession().createCriteria(clazz).add(Restrictions.between(pName, range.getMin(), range.getMax())).setProjection(Projections.max(pName)).uniqueResult());
    }
}
