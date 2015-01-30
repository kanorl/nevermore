package com.shadow.entity.cache;

import com.google.common.cache.*;
import com.google.common.collect.Sets;
import com.shadow.entity.CacheableEntity;
import com.shadow.entity.EntityFactory;
import com.shadow.entity.IEntity;
import com.shadow.entity.cache.annotation.CacheSize;
import com.shadow.entity.cache.annotation.Cacheable;
import com.shadow.entity.cache.annotation.PreLoaded;
import com.shadow.entity.orm.DataAccessor;
import com.shadow.entity.orm.persistence.PersistenceProcessor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

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

    @Nullable
    @Override
    public V get(@Nonnull K id) {
        requireNonNull(id);
        try {
            return cache.get(id);
        } catch (CacheLoader.InvalidCacheLoadException e) {
            return null;
        } catch (ExecutionException e) {
            // should never reach here
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Nonnull
    @Override
    public V getOrCreate(@Nonnull K id, @Nonnull EntityFactory<V> factory) {
        requireNonNull(id);
        requireNonNull(factory);
        try {
            return cache.get(id, () -> {
                V entity = cacheLoader.load(id);
                if (entity != null) {
                    return entity;
                }

                entity = factory.newInstance();
                if (!id.equals(entity.getId())) {
                    throw new IllegalArgumentException("ID不一致: id=" + id + ", factory.newInstance().getId()=" + entity.getId());
                }

                V cachedEntity = entityWrapper.wrap(entity);

                ((CachedEntity) cachedEntity).postEdit();// mark as modified
                updating.put(cachedEntity.getId(), cachedEntity);

                persistenceProcessor.save(cachedEntity, () -> {
                    if (((CachedEntity) cachedEntity).isPersisted()) {
                        updating.remove(cachedEntity.getId());
                    }
                });
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
            updating.put(entity.getId(), entity);
        }

        persistenceProcessor.update(entity, () -> {
            if (entity instanceof CachedEntity && ((CachedEntity) entity).isPersisted()) {
                updating.remove(entity.getId());
            }
        });
        return true;
    }

    @Override
    public void remove(@Nonnull K id) {
        requireNonNull(id);
        cache.invalidate(id);
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

            V entity = updating.get(key);
            if (entity != null) {
                return entityWrapper.wrap(entity);
            }

            entity = dataAccessor.get(key, clazz);
            if (entity == null) {
                return null;
            }
            return entityWrapper.wrap(entity);
        }
    }

    private class DbRemovalListener implements RemovalListener<K, V> {

        @Override
        public void onRemoval(@Nonnull RemovalNotification<K, V> notification) {
            if (notification.wasEvicted()) {
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("实体类 [{}] 缓存清理数据: Cause={}, id={}", clazz.getSimpleName(), notification.getCause(), notification.getKey());
                }
                return;
            }

            K id = notification.getKey();
            V value = notification.getValue();

            removing.add(id);
            persistenceProcessor.delete(value, () -> removing.remove(id));
        }
    }
}
