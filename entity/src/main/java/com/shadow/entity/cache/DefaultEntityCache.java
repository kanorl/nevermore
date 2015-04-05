package com.shadow.entity.cache;

import com.google.common.cache.*;
import com.google.common.collect.Sets;
import com.shadow.entity.CacheableEntity;
import com.shadow.entity.IEntity;
import com.shadow.entity.cache.annotation.CacheSize;
import com.shadow.entity.cache.annotation.Cacheable;
import com.shadow.entity.cache.annotation.PreLoaded;
import com.shadow.entity.orm.DataAccessor;
import com.shadow.entity.orm.persistence.PersistenceProcessor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * 实体类内存缓存实现
 *
 * @author nevermore on 2014/11/26.
 */
public class DefaultEntityCache<K extends Serializable, V extends IEntity<K>> implements EntityCache<K, V> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultEntityCache.class);

    protected final Class<V> clazz;
    private final DataAccessor dataAccessor;
    private final PersistenceProcessor<V> persistenceProcessor;
    private final LoadingCache<K, V> cache;
    private final CacheLoader<K, V> cacheLoader = new DbLoader();
    private final Set<K> removing = Sets.newConcurrentHashSet();
    private final CachedEntityWrapper<K, V> entityWrapper;
    private final ConcurrentMap<K, V> updating = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public DefaultEntityCache(Class<? extends IEntity<?>> clazz, DataAccessor dataAccessor, PersistenceProcessor<? extends IEntity<?>> persistenceProcessor) {
        this.clazz = (Class<V>) clazz;
        this.dataAccessor = dataAccessor;
        this.persistenceProcessor = (PersistenceProcessor<V>) persistenceProcessor;

        // 代理类生成器
        entityWrapper = new CachedEntityWrapper<>(this, this.clazz);

        // 构建缓存
        Cacheable cacheable = clazz.isAnnotationPresent(Cacheable.class) ? clazz.getAnnotation(Cacheable.class) : CacheableEntity.class.getAnnotation(Cacheable.class);
        String cacheSpec = toCacheSpec(cacheable);
        cache = CacheBuilder.from(cacheSpec).removalListener(new DbRemovalListener()).build(cacheLoader);

        // 预加载数据
        PreLoaded preLoaded = clazz.getAnnotation(PreLoaded.class);
        if (preLoaded != null) {
            preLoaded.policy().load(dataAccessor, this.clazz).stream().forEach(v -> cache.put(v.getId(), v));
        }
    }

    @Nonnull
    @Override
    public V create(V entity) {
        requireNonNull(entity);
        requireNonNull(entity.getId(), "ID不能为null");
        V entity2 = getOrCreate(entity.getId(), () -> entity);
        V obj = entity2 instanceof CachedEntity ? ((CachedEntity) entity2).getEntity() : entity2;
        if (entity != obj) {
            throw new DuplicateKeyException("重复的主键[" + entity.getId() + "]");
        }
        return entity2;
    }

    @Nonnull
    @Override
    public Optional<V> get(@Nonnull K id) {
        requireNonNull(id);
        try {
            return Optional.of(cache.get(id));
        } catch (CacheLoader.InvalidCacheLoadException e) {
            return Optional.empty();
        } catch (ExecutionException e) {
            // should never reach here
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Nonnull
    @Override
    public V getOrCreate(@Nonnull K id, @Nonnull Supplier<V> factory) {
        requireNonNull(id);
        requireNonNull(factory);
        try {
            return cache.get(id, () -> {
                V entity = cacheLoader.load(id);
                if (entity != null) {
                    return entity;
                }

                entity = factory.get();
                if (!id.equals(entity.getId())) {
                    throw new IllegalArgumentException("ID不一致: expected=" + id + ", given=" + entity.getId());
                }

                V cachedEntity = entityWrapper.wrap(entity);
                ((CachedEntity) cachedEntity).postEdit();// mark as modified
                persistenceProcessor.save(cachedEntity);
                return cachedEntity;
            });
        } catch (ExecutionException e) {
            // should never reach here
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean update(@Nonnull V entity) {
        requireNonNull(entity);
        if (removing.contains(entity.getId())) {
            LOGGER.error("无法更新已经被删除的数据：clazz={}, id={}", clazz.getSimpleName(), entity.getId());
            return false;
        }
        if (entity instanceof CachedEntity) {
            ((CachedEntity) entity).postEdit();// mark as modified
        }

        persistenceProcessor.update(entity);
        return true;
    }

    @Override
    public void remove(@Nonnull K id) {
        requireNonNull(id);
        get(id).ifPresent(entity -> {
            if (entity instanceof CachedEntity) {
                ((CachedEntity) entity).postEdit();// mark as modified
            }
            cache.invalidate(id);
        });
        cache.invalidateAll();
    }

    private String toCacheSpec(Cacheable cacheable) {
        StringBuilder spec = new StringBuilder(150);
        int maximumSize = Math.max((int) (cacheable.cacheSize().size().get() * cacheable.cacheSize().factor()), CacheSize.Size.MINIMUM.get());
        int initialCapacity = maximumSize >> 1;
        spec.append("initialCapacity").append("=").append(initialCapacity).append(",");
        spec.append("maximumSize").append("=").append(maximumSize).append(",");
        spec.append("concurrencyLevel").append("=").append(cacheable.concurrencyLevel());
        if (StringUtils.isNotEmpty(cacheable.expireAfterAccess())) {
            spec.append(",").append("expireAfterAccess").append("=").append(cacheable.expireAfterAccess());
        }
        if (StringUtils.isNotEmpty(cacheable.expireAfterWrite())) {
            spec.append(",").append("expireAfterWrite").append("=").append(cacheable.expireAfterWrite());
        }
        if (cacheable.recordStats()) {
            spec.append(",").append("recordStats");
        }
        if (cacheable.weakKeys()) {
            spec.append(",").append("weakKeys");
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("实体类 [{}] 缓存配置: {}", clazz.getSimpleName(), spec);
        }

        return spec.toString();
    }

    private class DbLoader extends CacheLoader<K, V> {

        @Override
        public V load(@Nonnull K key) throws Exception {
            if (removing.contains(key)) {
                LOGGER.error("无法加载已经被删除的数据：clazz={}, id={}", clazz.getSimpleName(), key);
                return null;
            }

            V dbEntity = dataAccessor.get(key, clazz);
            V updatingEntity = updating.remove(key);
            if (updatingEntity != null) {
                return entityWrapper.wrap(updatingEntity);
            }

            return dbEntity == null ? null : entityWrapper.wrap(dbEntity);
        }
    }

    private class DbRemovalListener implements RemovalListener<K, V> {

        @Override
        public void onRemoval(@Nonnull RemovalNotification<K, V> notification) {
            K id = notification.getKey();
            V value = notification.getValue();
            if (id == null || value == null) {
                return;
            }

            if (notification.wasEvicted()) {
                if (value instanceof CachedEntity && !((CachedEntity) value).isPersisted()) {
                    updating.put(id, value);
                    LOGGER.error("缓存失效且数据未入库: id={}, class={}", id, clazz.getSimpleName());
                }
                return;
            }


            removing.add(id);
            persistenceProcessor.delete(value, () -> removing.remove(id));
        }
    }
}
