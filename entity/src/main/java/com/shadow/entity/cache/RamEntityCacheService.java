package com.shadow.entity.cache;

import com.google.common.cache.*;
import com.google.common.collect.Sets;
import com.shadow.entity.CachedEntity;
import com.shadow.entity.EntityFactory;
import com.shadow.entity.IEntity;
import com.shadow.entity.annotation.AutoSave;
import com.shadow.entity.annotation.PreLoaded;
import com.shadow.entity.cache.annotation.Cached;
import com.shadow.entity.orm.DataAccessor;
import com.shadow.entity.orm.persistence.PersistenceProcessor;
import com.shadow.entity.proxy.EntityProxyGenerator;
import com.shadow.entity.proxy.NullEntityProxyGenerator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * 内存实体缓存实现
 *
 * @author nevermore on 2014/11/26.
 */
public class RamEntityCacheService<K extends Serializable, V extends IEntity<K>> implements EntityCacheService<K, V> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RamEntityCacheService.class);

    private final Class<V> clazz;
    private final DataAccessor dataAccessor;
    private final PersistenceProcessor<V> persistenceProcessor;
    private final LoadingCache<K, V> cache;
    private final Set<K> waitingRemoval = Sets.newConcurrentHashSet();
    private final EntityProxyGenerator<K, V> proxyGenerator;

    public RamEntityCacheService(Class<V> clazz, DataAccessor dataAccessor, PersistenceProcessor<V> persistenceProcessor) {
        this.clazz = clazz;
        this.dataAccessor = dataAccessor;
        this.persistenceProcessor = persistenceProcessor;

        // 代理类生成器
        if (Arrays.stream(clazz.getDeclaredMethods()).filter(method -> method.isAnnotationPresent(AutoSave.class)).findAny().isPresent()) {
            proxyGenerator = new EntityProxyGenerator<>(this);
        } else {
            proxyGenerator = new NullEntityProxyGenerator<>();
        }

        // 构建缓存
        Cached cached = clazz.isAnnotationPresent(Cached.class) ? clazz.getAnnotation(Cached.class) : CachedEntity.class.getAnnotation(Cached.class);
        String cacheSpec = toCacheSpec(cached);
        cache = CacheBuilder.from(cacheSpec).removalListener(new DbRemovalListener()).build(new DbLoader());

        // 预加载数据
        PreLoaded preLoaded = clazz.getAnnotation(PreLoaded.class);
        if (preLoaded != null) {
            preLoaded.type().load(dataAccessor, preLoaded.queryName(), clazz).stream().forEach((V v) -> cache.put(v.getId(), v));
        }
    }

    @Override
    public V get(@Nonnull K id) {
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

    @Override
    public V getOr(@Nonnull K id, @Nonnull EntityFactory<V> factory) {
        try {
            return cache.get(id, () -> {
                V v = cache.get(id);
                if (v != null) {
                    return v;
                }

                V newObj = factory.newInstance();
                persistenceProcessor.save(newObj);
                return newObj;
            });
        } catch (ExecutionException e) {
            // should never reach here
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(@Nonnull V v) {
        if (waitingRemoval.contains(v.getId())) {
            LOGGER.error("Update failed: Attempting to update a object which is waiting to be deleted.");
            return;
        }
        persistenceProcessor.update(v);
    }

    @Override
    public void remove(@Nonnull V v) {
        cache.invalidate(v.getId());
    }

    private String toCacheSpec(Cached cached) {
        StringBuilder spec = new StringBuilder();
        spec.append("initialCapacity").append("=").append(cached.initialCapacity()).append(",");
        spec.append("maximumSize").append("=").append(cached.maximumSize()).append(",");
        spec.append("concurrencyLevel").append("=").append(cached.concurrencyLevel());
        if (StringUtils.isNotEmpty(cached.expireAfterAccess())) {
            spec.append(",").append("expireAfterAccess").append("=").append(cached.expireAfterAccess());
        }
        if (StringUtils.isNotEmpty(cached.expireAfterWrite())) {
            spec.append(",").append("expireAfterWrite").append("=").append(cached.expireAfterWrite());
        }
        if (cached.recordStats()) {
            spec.append(",").append("recordStats");
        }
        if (cached.weakKeys()) {
            spec.append(",").append("weakKeys");
        }
        return spec.toString();
    }

    private class DbLoader extends CacheLoader<K, V> {

        @Override
        public V load(@Nonnull K key) throws Exception {
            if (waitingRemoval.contains(key)) {
                return null;
            }
            V v = dataAccessor.get(key, clazz);
            if (v == null) {
                return null;
            }
            return proxyGenerator.generate(v);
        }
    }

    private class DbRemovalListener implements RemovalListener<K, V> {

        @Override
        public void onRemoval(@Nonnull RemovalNotification<K, V> notification) {
            if (notification.getCause() == RemovalCause.EXPIRED) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Expire entry from Cache(not the DB): " + notification);
                }
                return;
            }

            K id = notification.getKey();
            V value = notification.getValue();
            if (id == null || value == null) {
                return;
            }

            waitingRemoval.add(id);
            persistenceProcessor.delete(value, () -> waitingRemoval.remove(id));
        }
    }
}
