package com.shadow.entity.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Sets;
import com.shadow.entity.EntityFactory;
import com.shadow.entity.IEntity;
import com.shadow.entity.annotation.CacheIndex;
import com.shadow.entity.orm.DataAccessor;
import com.shadow.entity.orm.persistence.PersistenceProcessor;
import com.shadow.entity.proxy.EntityProxy;
import org.apache.commons.lang3.ClassUtils;
import org.reflections.ReflectionUtils;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

/**
 * @author nevermore on 2015/1/5
 */
@SuppressWarnings("unchecked")
public class DefaultRegionEntityCache<K extends Serializable, V extends IEntity<K>> extends DefaultEntityCache<K, V> implements RegionEntityCache<K, V> {

    private final LoadingCache<Object, Set<K>> indexCache;
    private final Field indexField;

    public DefaultRegionEntityCache(Class<V> clazz, DataAccessor dataAccessor, PersistenceProcessor<V> persistenceProcessor) {
        super(clazz, dataAccessor, persistenceProcessor);

        this.indexField = ReflectionUtils.getAllFields(clazz, field -> field.isAnnotationPresent(CacheIndex.class)).stream().findFirst().get();
        indexField.setAccessible(true);

        this.indexCache = CacheBuilder.newBuilder().concurrencyLevel(16).expireAfterAccess(30, TimeUnit.MINUTES).build(new CacheLoader<Object, Set<K>>() {
            @Override
            public Set<K> load(@Nonnull Object value) throws Exception {
                return Sets.newConcurrentHashSet(dataAccessor.queryId(clazz, Collections.singletonMap(indexField.getName(), value)));
            }
        });
    }

    @Nonnull
    @Override
    public V getOrCreate(@Nonnull K id, @Nonnull EntityFactory<V> factory) {
        V obj = super.getOrCreate(id, factory);
        Object value = getIndexValue(obj);
        indexCache.getUnchecked(value).add(obj.getId());
        return obj;
    }

    @Nonnull
    @Override
    public Collection<V> list(@Nonnull Object indexValue) {
        validate(indexValue);
        return Collections.unmodifiableCollection(
                indexCache.getUnchecked(indexValue).stream().collect(HashSet<V>::new, (vs, k) -> {
                    V v = get(k);
                    if (v != null) {
                        vs.add(v);
                    }
                }, HashSet<V>::addAll)
        );
    }

    @Override
    public void updateWithIndexValueChanged(@Nonnull V v, @Nonnull Object oldIndexValue) {
        if (!update(v)) {
            return;
        }

        validate(oldIndexValue);

        indexCache.getUnchecked(oldIndexValue).remove(v.getId());
        Object newValue = getIndexValue(v);
        indexCache.getUnchecked(newValue).add(v.getId());
    }

    @Override
    public void remove(@Nonnull K id) {
        V v = get(id);
        if (v == null) {
            return;
        }
        super.remove(id);
        Object value = getIndexValue(v);
        indexCache.getUnchecked(value).remove(v.getId());
    }

    public Object getIndexValue(@Nonnull V v) {
        requireNonNull(v);
        IEntity<?> entity = v instanceof EntityProxy ? ((EntityProxy) v).getEntity() : v;
        try {
            return indexField.get(entity);
        } catch (IllegalAccessException e) {
            // won't happen
            throw new IllegalStateException(e);
        }
    }

    private void validate(Object value) {
        requireNonNull(value, "");
        checkArgument(ClassUtils.isAssignable(indexField.getType(), value.getClass(), true), "");
    }
}
