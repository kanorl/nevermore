package com.shadow.entity.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.shadow.entity.IEntity;
import com.shadow.entity.annotation.RegionIndex;
import com.shadow.entity.orm.DataAccessor;
import com.shadow.entity.orm.persistence.PersistenceProcessor;
import org.reflections.ReflectionUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @author nevermore on 2015/1/5
 */
@SuppressWarnings("unchecked")
public class RegionEntityCacheImpl<K extends Serializable, V extends IEntity<K>> implements RegionEntityCache<K, V> {

    private final DataAccessor dataAccessor;
    private final Class<V> clazz;
    private final PersistenceProcessor<V> persistenceProcessor;
    private final Map<String, Field> indexFields;

    public RegionEntityCacheImpl(Class<V> clazz, DataAccessor dataAccessor, PersistenceProcessor<V> persistenceProcessor) {
        this.clazz = clazz;
        this.dataAccessor = dataAccessor;
        this.persistenceProcessor = persistenceProcessor;
        this.indexFields = Maps.uniqueIndex(ReflectionUtils.getAllFields(clazz, field -> field.isAnnotationPresent(RegionIndex.class)), Field::getName);
    }

    private LoadingCache<String, LoadingCache<Object, LoadingCache<K, V>>> cache = CacheBuilder.newBuilder().build(new CacheLoader<String, LoadingCache<Object, LoadingCache<K, V>>>() {
        @Override
        public LoadingCache<Object, LoadingCache<K, V>> load(String key) throws Exception {
            return CacheBuilder.newBuilder().build(new CacheLoader<Object, LoadingCache<K, V>>() {
                @Override
                public LoadingCache<K, V> load(Object key) throws Exception {
                    return CacheBuilder.newBuilder().build(new CacheLoader<K, V>() {
                        @Override
                        public V load(K key) throws Exception {
                            return null;
                        }
                    });
                }
            });
        }
    });

    @Override
    public Collection<V> getAll(String property, Object value) {
        try {
            return Collections.unmodifiableCollection(cache.get(property).get(value).asMap().values());
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public K create(V v) {
        return null;
    }
}
