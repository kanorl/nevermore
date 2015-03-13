package com.shadow.entity.orm;

import com.google.common.collect.Range;
import com.shadow.entity.IEntity;
import org.hibernate.criterion.Projection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 数据访问器接口
 *
 * @author nevermore on 2014/11/26.
 */
public interface DataAccessor {

    @Nullable
    <K extends Serializable, V extends IEntity<K>> V get(@Nonnull K id, @Nonnull Class<V> clazz);

    <K extends Serializable, V extends IEntity<K>> K save(@Nonnull V v);

    <V extends IEntity<?>> void update(@Nonnull V v);

    <V extends IEntity<?>> void delete(@Nonnull V v);

    <V extends IEntity<?>> void saveOrUpdate(@Nonnull V v);

    <E> List<E> namedQuery(@Nonnull String queryName, @Nullable Object... queryParams);

    @Nonnull
    <V extends IEntity<?>> List<V> query(@Nonnull Class<V> clazz);

    @Nonnull
    <V extends IEntity<?>, T> List<T> query(@Nonnull Class<V> clazz, @Nullable Map<String, ?> propertyNameValues, @Nullable Projection... projections);

    @Nonnull
    <K extends Serializable, V extends IEntity<K>> List<K> queryIds(@Nonnull Class<V> clazz, @Nonnull Map<String, Object> stringObjectMap);

    <K extends Serializable, V extends IEntity<K>> Optional<K> queryMaxId(@Nonnull Class<V> clazz, Range<?> range);
}
