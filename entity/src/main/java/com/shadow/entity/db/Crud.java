package com.shadow.entity.db;

import com.shadow.entity.IEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;

/**
 * @author nevermore on 2015/5/2.
 */
public interface Crud {

    @Nullable
    <K extends Serializable, V extends IEntity<K>> V get(@Nonnull K id, @Nonnull Class<V> clazz);

    <K extends Serializable, V extends IEntity<K>> void save(@Nonnull V v);

    <V extends IEntity<?>> void update(@Nonnull V v);

    <V extends IEntity<?>> void delete(@Nonnull V v);
}
