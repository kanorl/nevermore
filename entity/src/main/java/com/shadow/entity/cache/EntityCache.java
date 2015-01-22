package com.shadow.entity.cache;

import com.shadow.entity.EntityFactory;
import com.shadow.entity.IEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;

/**
 * 实体类缓存接口
 *
 * @author nevermore on 2014/11/26.
 */
public interface EntityCache<K extends Serializable, V extends IEntity<K>> {

    @Nullable
    V get(@Nonnull K id);

    @Nonnull
    V getOrCreate(@Nonnull K id, @Nonnull EntityFactory<V> factory);

    boolean update(@Nonnull V entity);

    void remove(@Nonnull K id);
}
