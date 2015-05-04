package com.shadow.entity.db;

import com.google.common.collect.Range;
import com.shadow.entity.IEntity;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * @author nevermore on 2015/5/2.
 */
public interface Repository extends Crud {

    @Nonnull
    <K extends Serializable & Comparable<K>, V extends IEntity<K>> Optional<K> getMaxId(@Nonnull Class<V> clazz, @Nonnull Range<K> range);

    @Nonnull
    <K extends Serializable, V extends IEntity<K>> List<K> getIds(@Nonnull Class<V> clazz, @Nonnull String field, Object value);

    <K extends Serializable, V extends IEntity<K>> List<V> query(@Nonnull Class<V> clazz, @Nonnull String sql);
}
