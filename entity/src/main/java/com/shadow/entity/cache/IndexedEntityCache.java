package com.shadow.entity.cache;

import com.shadow.entity.EntityFactory;
import com.shadow.entity.IEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Collection;

/**
 * @author nevermore on 2015/1/5
 */
public interface IndexedEntityCache<K extends Serializable, V extends IEntity<K>> {

    @Nullable
    V get(@Nonnull K id);

    V getOrCreate(@Nonnull K id, @Nonnull EntityFactory<V> factory);

    @Nonnull
    Collection<V> getAll(@Nonnull IndexEntry indexEntry);

    void update(@Nonnull V v);

    void updateWithIndexValueChanged(@Nonnull V v, @Nonnull IndexEntry... previousIndexes);

    void remove(@Nonnull V v);
}
