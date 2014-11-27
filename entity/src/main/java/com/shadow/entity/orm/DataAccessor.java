package com.shadow.entity.orm;

import com.shadow.entity.IEntity;

import java.io.Serializable;
import java.util.List;

/**
 * 数据访问器接口
 *
 * @author nevermore on 2014/11/26.
 */
public interface DataAccessor {

    <K extends Serializable, V extends IEntity<K>> V get(K id, Class<V> clazz);

    <K extends Serializable, V extends IEntity<K>> K save(V v);

    <V extends IEntity<?>> void update(V v);

    <V extends IEntity<?>> void delete(V v);

    <V extends IEntity<?>> List<V> getAll(Class<V> clazz);

    <V extends IEntity<?>> List<V> namedQuery(Class<V> clazz, String queryName, Object... queryParams);
}
