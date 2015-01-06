package com.shadow.entity.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.shadow.entity.EntityFactory;
import com.shadow.entity.IEntity;
import com.shadow.entity.cache.annotation.CacheIndex;
import com.shadow.entity.orm.DataAccessor;
import com.shadow.entity.orm.persistence.PersistenceProcessor;
import com.shadow.entity.proxy.EntityProxy;
import org.apache.commons.lang3.ClassUtils;
import org.reflections.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

/**
 * @author nevermore on 2015/1/5
 */
@SuppressWarnings("unchecked")
public class IndexedEntityCacheImpl<K extends Serializable, V extends IEntity<K>> implements IndexedEntityCache<K, V> {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexedEntityCacheImpl.class);

    private final Map<String, Field> indexFields;
    private final EntityCache<K, V> entityCache;
    private final LoadingCache<String, LoadingCache<Object, Set<K>>> indexCache;

    public IndexedEntityCacheImpl(Class<V> clazz, DataAccessor dataAccessor, PersistenceProcessor<V> persistenceProcessor) {
        this.indexFields = Maps.uniqueIndex(ReflectionUtils.getAllFields(clazz, field -> field.isAnnotationPresent(CacheIndex.class)), Field::getName);
        this.entityCache = new EntityCacheImpl<>(clazz, dataAccessor, persistenceProcessor);

        // make index field accessible
        indexFields.values().forEach(field -> field.setAccessible(true));

        indexCache = CacheBuilder.newBuilder().concurrencyLevel(16).initialCapacity(indexFields.size()).build(new CacheLoader<String, LoadingCache<Object, Set<K>>>() {
            @Override
            public LoadingCache<Object, Set<K>> load(@Nonnull String property) throws Exception {
                return CacheBuilder.newBuilder().concurrencyLevel(16).expireAfterAccess(30, TimeUnit.MINUTES).build(new CacheLoader<Object, Set<K>>() {
                    @Override
                    public Set<K> load(@Nonnull Object value) throws Exception {
                        return Sets.newConcurrentHashSet(dataAccessor.queryId(clazz, Collections.singletonMap(property, value)));
                    }
                });
            }
        });
    }

    @Nullable
    @Override
    public V get(@Nonnull K id) {
        return entityCache.get(id);
    }

    @Override
    public V getOrCreate(@Nonnull K id, @Nonnull EntityFactory<V> factory) {
        V obj = entityCache.getOrCreate(id, factory);
        indexFields.forEach((property, field) -> {
            try {
                Object value = getIndexValue(field, obj);
                indexCache.getUnchecked(property).getUnchecked(value).add(obj.getId());
            } catch (IllegalAccessException e) {
                // won't happen
            }
        });
        return obj;
    }

    @Nonnull
    @Override
    public Collection<V> getAll(@Nonnull IndexEntry indexEntry) {
        validate(indexEntry);
        return Collections.unmodifiableCollection(
                indexCache.getUnchecked(indexEntry.getName()).getUnchecked(indexEntry.getValue()).stream().collect(HashSet<V>::new, (vs, k) -> {
                    V v = entityCache.get(k);
                    if (v != null) {
                        vs.add(v);
                    }
                }, HashSet<V>::addAll)
        );
    }

    @Override
    public void update(@Nonnull V v) {
        entityCache.update(v);
    }

    @Override
    public void updateWithIndexValueChanged(@Nonnull V v, @Nonnull IndexEntry... previousIndexes) {
        if (!entityCache.update(v)) {
            return;
        }

        validate(previousIndexes);

        for (IndexEntry indexEntry : previousIndexes) {
            indexCache.getUnchecked(indexEntry.getName()).getUnchecked(indexEntry.getValue()).remove(v.getId());
            try {
                Object newValue = getIndexValue(indexEntry.getName(), v);
                indexCache.getUnchecked(indexEntry.getName()).getUnchecked(newValue).add(v.getId());
            } catch (IllegalAccessException e) {
                // won't happen
            }
        }
    }

    @Override
    public void remove(@Nonnull V v) {
        entityCache.remove(v.getId());
        indexFields.forEach((property, field) -> {
            try {
                Object value = getIndexValue(field, v);
                indexCache.getUnchecked(property).getUnchecked(value).remove(v.getId());
            } catch (IllegalAccessException e) {
                // won't happen
            }
        });
    }

    private Object getIndexValue(String name, V v) throws IllegalAccessException {
        Field field = indexFields.get(name);
        if (field == null) {
            throw new NullPointerException();
        }
        return getIndexValue(field, v);
    }

    private Object getIndexValue(Field field, V v) throws IllegalAccessException {
        requireNonNull(field);
        IEntity<?> entity = v instanceof EntityProxy ? ((EntityProxy) v).getEntity() : v;
        return field.get(entity);
    }

    private void validate(IndexEntry... indexEntries) {
        requireNonNull(indexEntries, "");
        for (IndexEntry indexEntry : indexEntries) {
            checkArgument(indexFields.get(indexEntry.getName()) != null, "");
            checkArgument(ClassUtils.isAssignable(indexFields.get(indexEntry.getName()).getType(), indexEntry.getValue().getClass(), true), "");
        }
    }
}
