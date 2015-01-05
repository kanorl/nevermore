package com.shadow.entity.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.shadow.entity.IEntity;
import com.shadow.entity.annotation.IndexedProperty;
import com.shadow.entity.orm.DataAccessor;
import com.shadow.entity.orm.persistence.PersistenceProcessor;
import org.reflections.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @author nevermore on 2015/1/5
 */
@SuppressWarnings("unchecked")
public class IndexedEntityCacheImpl<K extends Serializable, V extends IEntity<K>> implements IndexedEntityCache<K, V> {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexedEntityCacheImpl.class);

    private final DataAccessor dataAccessor;
    private final Class<V> clazz;
    private final Map<String, Field> indexFields;
    private final EntityCache<K, V> entityCache;

    public IndexedEntityCacheImpl(Class<V> clazz, DataAccessor dataAccessor, PersistenceProcessor<V> persistenceProcessor) {
        this.clazz = clazz;
        this.dataAccessor = dataAccessor;
        this.indexFields = Maps.uniqueIndex(ReflectionUtils.getAllFields(clazz, field -> field.isAnnotationPresent(IndexedProperty.class)), Field::getName);
        entityCache = new EntityCacheImpl<>(clazz, dataAccessor, persistenceProcessor);

        // make index field accessible
        indexFields.values().forEach(field -> field.setAccessible(true));
    }

    private LoadingCache<String, LoadingCache<Object, Set<K>>> cache = CacheBuilder.newBuilder().build(new CacheLoader<String, LoadingCache<Object, Set<K>>>() {
        @Override
        public LoadingCache<Object, Set<K>> load(@Nonnull String property) throws Exception {
            return CacheBuilder.newBuilder().build(new CacheLoader<Object, Set<K>>() {
                @Override
                public Set<K> load(@Nonnull Object value) throws Exception {
                    List<K> result = dataAccessor.queryId(clazz, Collections.singletonMap(property, value));
                    return Sets.newConcurrentHashSet(result);
                }
            });
        }
    });

    @Override
    public Collection<V> getAll(String property, Object value) {
        if (!indexFields.containsKey(property)) {
            throw new UnsupportedOperationException();
        }
        return Collections.unmodifiableCollection(
                cache.getUnchecked(property).getUnchecked(value).stream().collect(HashSet<V>::new, (vs, k) -> {
                    V v = entityCache.get(k);
                    if (v != null) {
                        vs.add(v);
                    }
                }, HashSet<V>::addAll)
        );
    }

    @Override
    public K create(V v) {
        V obj = entityCache.getOr(v.getId(), () -> v);
        indexFields.forEach((property, field) -> {
            try {
                Object value = field.get(obj);
                cache.getUnchecked(property).getUnchecked(value).add(obj.getId());
            } catch (IllegalAccessException e) {
                LOGGER.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        });
        return obj.getId();
    }
}
