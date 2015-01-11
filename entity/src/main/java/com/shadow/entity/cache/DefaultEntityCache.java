package com.shadow.entity.cache;

import com.google.common.cache.*;
import com.google.common.collect.Sets;
import com.shadow.entity.CacheableEntity;
import com.shadow.entity.EntityFactory;
import com.shadow.entity.IEntity;
import com.shadow.entity.annotation.AutoSave;
import com.shadow.entity.annotation.CacheSize;
import com.shadow.entity.annotation.Cacheable;
import com.shadow.entity.annotation.PreLoaded;
import com.shadow.entity.orm.DataAccessor;
import com.shadow.entity.orm.persistence.PersistenceProcessor;
import com.shadow.entity.proxy.DefaultEntityProxyGenerator;
import com.shadow.entity.proxy.EntityProxyGenerator;
import com.shadow.entity.proxy.NullEntityProxyGenerator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * 实体类内存缓存实现
 *
 * @author nevermore on 2014/11/26.
 */
public class DefaultEntityCache<K extends Serializable, V extends IEntity<K>> implements EntityCache<K, V> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultEntityCache.class);

    private final Class<V> clazz;
    private final DataAccessor dataAccessor;
    private final PersistenceProcessor<V> persistenceProcessor;
    private final LoadingCache<K, V> cache;
    private final CacheLoader<K, V> cacheLoader = new DbLoader();
    private final Set<K> waitingRemoval = Sets.newConcurrentHashSet();
    private final EntityProxyGenerator<K, V> proxyGenerator;

    public DefaultEntityCache(Class<V> clazz, DataAccessor dataAccessor, PersistenceProcessor<V> persistenceProcessor) {
        this.clazz = clazz;
        this.dataAccessor = dataAccessor;
        this.persistenceProcessor = persistenceProcessor;

        // 代理类生成器
        proxyGenerator = Arrays.stream(clazz.getDeclaredMethods()).anyMatch((m) -> m.isAnnotationPresent(AutoSave.class)) ? new DefaultEntityProxyGenerator<>(this, clazz) : new NullEntityProxyGenerator<>();

        // 构建缓存
        Cacheable cacheable = clazz.isAnnotationPresent(Cacheable.class) ? clazz.getAnnotation(Cacheable.class) : CacheableEntity.class.getAnnotation(Cacheable.class);
        String cacheSpec = toCacheSpec(cacheable);
        cache = CacheBuilder.from(cacheSpec).removalListener(new DbRemovalListener()).build(cacheLoader);

        // 预加载数据
        PreLoaded preLoaded = clazz.getAnnotation(PreLoaded.class);
        if (preLoaded != null) {
            preLoaded.policy().load(dataAccessor, clazz).stream().forEach(v -> cache.put(v.getId(), v));
        }
    }

    @Nullable
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

    @Nonnull
    @Override
    public V getOrCreate(@Nonnull K id, @Nonnull EntityFactory<V> factory) {
        try {
            return cache.get(id, () -> {
                V v = cacheLoader.load(id);
                if (v != null) {
                    return v;
                }

                V newObj = factory.newInstance();
                persistenceProcessor.save(newObj);
                return proxyGenerator.generate(newObj);
            });
        } catch (ExecutionException e) {
            // should never reach here
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean update(@Nonnull V v) {
        if (waitingRemoval.contains(v.getId())) {
            LOGGER.error("无法更新已经被删除的数据：clazz={}, id={}", clazz.getSimpleName(), v.getId());
            return false;
        }
        persistenceProcessor.update(v);
        return true;
    }

    @Override
    public void remove(@Nonnull K id) {
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
            if (waitingRemoval.contains(key)) {
                LOGGER.error("无法加载已经被删除的数据：clazz={}, id={}", clazz.getSimpleName(), key);
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
            if (notification.getCause() != RemovalCause.EXPLICIT) {
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("实体类 [{}] 缓存清理数据: Cause={}, id={}", clazz.getSimpleName(), notification.getCause(), notification.getKey());
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
