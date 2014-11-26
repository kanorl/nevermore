package com.shadow.entity.cache;

import com.shadow.entity.EntityFactory;
import com.shadow.entity.IEntity;

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 * 实体类缓存接口
 *
 * @author nevermore on 2014/11/26.
 */
public interface EntityCacheService<K extends Serializable, V extends IEntity<K>> {

    V get(@Nonnull K id);

    V getOr(@Nonnull K id, @Nonnull EntityFactory<V> factory);

    void update(@Nonnull V v);

    void remove(@Nonnull V v);
}
