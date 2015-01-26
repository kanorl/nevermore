package com.shadow.entity.cache;

import com.shadow.entity.IEntity;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Collection;

/**
 * @author nevermore on 2015/1/5
 */
public interface RegionEntityCache<K extends Serializable, V extends IEntity<K>> extends EntityCache<K, V> {

    V create(@Nonnull V entity);

    @Nonnull
    Collection<V> list(@Nonnull Object indexValue);

    void updateWithIndexValueChanged(@Nonnull V entity, @Nonnull Object oldIndexValue);

    Object getIndexValue(@Nonnull V entity);
}
