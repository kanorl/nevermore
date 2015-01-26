package com.shadow.entity.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Sets;
import com.shadow.entity.EntityFactory;
import com.shadow.entity.IEntity;
import com.shadow.entity.cache.annotation.CacheIndex;
import com.shadow.entity.cache.annotation.CacheSize;
import com.shadow.entity.orm.DataAccessor;
import com.shadow.entity.orm.persistence.PersistenceProcessor;
import com.shadow.entity.proxy.VersionedEntityProxy;
import org.apache.commons.lang3.ClassUtils;
import org.reflections.ReflectionUtils;
import org.springframework.dao.DuplicateKeyException;

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
import static org.slf4j.helpers.MessageFormatter.format;

/**
 * @author nevermore on 2015/1/5
 */
@SuppressWarnings("unchecked")
public class DefaultRegionEntityCache<K extends Serializable, V extends IEntity<K>> extends DefaultEntityCache<K, V> implements RegionEntityCache<K, V> {

    private final LoadingCache<Object, Set<K>> indexCache;
    private final Field indexField;

    public DefaultRegionEntityCache(Class<? extends IEntity<?>> clazz, DataAccessor dataAccessor, PersistenceProcessor<? extends IEntity<?>> persistenceProcessor) {
        super(clazz, dataAccessor, persistenceProcessor);

        indexField = ReflectionUtils.getAllFields(clazz, field -> field.isAnnotationPresent(CacheIndex.class)).stream().findFirst()
                .orElseThrow(() -> new IllegalStateException(format("在{}中找不到注解为{}的属性", clazz.getSimpleName(), CacheIndex.class.getSimpleName()).getMessage()));
        indexField.setAccessible(true);

        this.indexCache = CacheBuilder.newBuilder().maximumSize(CacheSize.Size.DEFAULT.get()).concurrencyLevel(16).expireAfterAccess(30, TimeUnit.MINUTES).build(new CacheLoader<Object, Set<K>>() {
            @Override
            public Set<K> load(@Nonnull Object value) throws Exception {
                return Sets.newConcurrentHashSet(dataAccessor.queryIds(DefaultRegionEntityCache.this.clazz, Collections.singletonMap(indexField.getName(), value)));
            }
        });
    }

    @Override
    public V create(@Nonnull V v) {
        requireNonNull(v);
        requireNonNull(v.getId(), "ID不能为null");
        V entity = super.getOrCreate(v.getId(), () -> v);
        V obj = entity instanceof VersionedEntityProxy ? ((VersionedEntityProxy) entity).getEntity() : entity;
        if (v != obj) {
            throw new DuplicateKeyException("重复的主键[" + v.getId() + "]");
        }
        Object indexValue = getIndexValue(entity);
        indexCache.getUnchecked(indexValue).add(entity.getId());
        return entity;
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
    public void updateWithIndexValueChanged(@Nonnull V entity, @Nonnull Object oldIndexValue) {
        if (!update(entity)) {
            return;
        }

        validate(oldIndexValue);

        indexCache.getUnchecked(oldIndexValue).remove(entity.getId());
        Object newValue = getIndexValue(entity);
        indexCache.getUnchecked(newValue).add(entity.getId());
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

    public Object getIndexValue(@Nonnull V entity) {
        requireNonNull(entity);
        if (entity instanceof VersionedEntityProxy) {
            return ((VersionedEntityProxy) entity).getIndexValue();
        }
        try {
            return indexField.get(entity);
        } catch (IllegalAccessException e) {
            // won't happen
            throw new IllegalStateException(e);
        }
    }

    private void validate(Object value) {
        requireNonNull(value, "索引值不能为null");
        checkArgument(ClassUtils.isAssignable(indexField.getType(), value.getClass(), true), "索引值类型错误: expect [%s], given [%s]", indexField.getType(), value.getClass());
    }
}
