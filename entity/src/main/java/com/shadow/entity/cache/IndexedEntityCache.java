package com.shadow.entity.cache;

import com.shadow.entity.IEntity;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author nevermore on 2015/1/5
 */
public interface IndexedEntityCache<K extends Serializable, V extends IEntity<K>> {

    Collection<V> getAll(String fieldName, Object fieldValue);

    K create(V v);
}
