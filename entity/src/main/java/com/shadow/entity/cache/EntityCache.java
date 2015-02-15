package com.shadow.entity.cache;

import com.shadow.entity.IEntity;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * 实体类缓存接口
 *
 * @author nevermore on 2014/11/26.
 */
public interface EntityCache<K extends Serializable, V extends IEntity<K>> {

    @Nonnull
    V create(V entity);

    @Nonnull
    Optional<V> get(@Nonnull K id);

    @Nonnull
    V getOrCreate(@Nonnull K id, @Nonnull Supplier<V> factory);

    boolean update(@Nonnull V entity);

    void remove(@Nonnull K id);
}
