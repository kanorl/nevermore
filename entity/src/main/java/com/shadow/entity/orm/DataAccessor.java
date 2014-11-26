package com.shadow.entity.orm;

import com.shadow.entity.IEntity;

import java.io.Serializable;

/**
 * 数据访问器接口
 *
 * @author nevermore on 2014/11/26.
 */
public interface DataAccessor {

    <K extends Serializable, V extends IEntity<K>> V get(K id, Class<V> clazz);

    <K extends Serializable, V extends IEntity<K>> K save(V v);

    <K extends Serializable, V extends IEntity<K>> void update(V v);

    <K extends Serializable, V extends IEntity<K>> void delete(V v);
}
