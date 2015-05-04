package com.shadow.entity.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Sets;
import com.shadow.entity.IEntity;
import com.shadow.entity.cache.annotation.CacheIndex;
import com.shadow.entity.cache.annotation.CacheSize;
import com.shadow.entity.db.Repository;
import com.shadow.entity.db.persistence.PersistenceProcessor;
import org.apache.commons.lang3.ClassUtils;
import org.reflections.ReflectionUtils;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;
import static org.slf4j.helpers.MessageFormatter.format;

/**
 * @author nevermore on 2015/1/5
 */
@SuppressWarnings("unchecked")
public class DefaultRegionEntityCache<K extends Serializable, V extends IEntity<K>> implements RegionEntityCache<K, V> {

    private final EntityCache<K, V> delegate;
    private final LoadingCache<Object, Set<K>> indexCache;
    private final Field indexField;

    public DefaultRegionEntityCache(Class<? extends IEntity<?>> clazz, Repository repository, PersistenceProcessor<? extends IEntity<?>> persistenceProcessor) {
        delegate = new DefaultEntityCache<>(clazz, repository, persistenceProcessor);

        indexField = ReflectionUtils.getAllFields(clazz, field -> field.isAnnotationPresent(CacheIndex.class)).stream().findFirst()
                .orElseThrow(() -> new IllegalStateException(format("在{}中找不到注解为{}的属性", clazz.getSimpleName(), CacheIndex.class.getSimpleName()).getMessage()));
        indexField.setAccessible(true);

        // todo confirm cache specification
        this.indexCache = CacheBuilder.newBuilder().maximumSize(CacheSize.Size.DEFAULT.get()).concurrencyLevel(16).expireAfterAccess(30, TimeUnit.MINUTES).build(new CacheLoader<Object, Set<K>>() {
            @Override
            public Set<K> load(@Nonnull Object value) throws Exception {
                return Sets.newConcurrentHashSet(repository.getIds((Class<V>) clazz, indexField.getName(), value));
            }
        });
    }

    @Nonnull
    @Override
    public Optional<V> get(@Nonnull K id) {
        return delegate.get(id);
    }

    @Nonnull
    @Override
    public V create(@Nonnull V v) {
        V entity = delegate.create(v);
        Object indexValue = getIndexValue(entity);
        indexCache.getUnchecked(indexValue).add(entity.getId());
        return entity;
    }

    @Nonnull
    @Override
    public V getOrCreate(@Nonnull K id, @Nonnull Supplier<V> factory) {
        V obj = delegate.getOrCreate(id, factory);
        Object value = getIndexValue(obj);
        indexCache.getUnchecked(value).add(obj.getId());
        return obj;
    }

    @Nonnull
    @Override
    public Collection<V> list(@Nonnull Object indexValue) {
        validate(indexValue);
        return indexCache.getUnchecked(indexValue).stream().map(k -> get(k).orElse(null)).filter(Objects::nonNull)
                .collect(Collectors.toSet());
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
    public boolean update(@Nonnull V entity) {
        return delegate.update(entity);
    }

    @Override
    public void remove(@Nonnull K id) {
        get(id).ifPresent(entity -> {
            delegate.remove(id);
            Object value = getIndexValue(entity);
            indexCache.getUnchecked(value).remove(entity.getId());
        });
    }

    public Object getIndexValue(@Nonnull V entity) {
        requireNonNull(entity);
        if (entity instanceof CachedEntity) {
            return ((CachedEntity) entity).getIndexValue();
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
        checkArgument(ClassUtils.isAssignable(indexField.getType(), value.getClass(), true), "索引值类型错误: expect [%s], given [%s]", indexField.getType().getName(), value.getClass().getName());
    }
}
