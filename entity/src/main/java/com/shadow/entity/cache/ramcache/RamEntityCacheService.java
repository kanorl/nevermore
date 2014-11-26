package com.shadow.entity.cache.ramcache;

import com.shadow.entity.cache.EntityCacheService;
import com.google.common.cache.*;
import com.google.common.collect.Sets;
import com.shadow.entity.EntityFactory;
import com.shadow.entity.IEntity;
import com.shadow.entity.orm.DataAccessor;
import com.shadow.entity.orm.persistence.PersistenceProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.Serializable;
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
    private final Set<K> removing = Sets.newConcurrentHashSet();

    public RamEntityCacheService(Class<V> clazz, DataAccessor dataAccessor, PersistenceProcessor<V> persistenceProcessor) {
        this.clazz = clazz;
        this.dataAccessor = dataAccessor;
        this.persistenceProcessor = persistenceProcessor;

        cache = CacheBuilder.newBuilder().removalListener(new DbRemovalListener()).build(new DbLoader());
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
        persistenceProcessor.update(v);
    }

    @Override
    public void remove(@Nonnull V v) {
        cache.invalidate(v.getId());
    }

    private class DbLoader extends CacheLoader<K, V> {

        @Override
        public V load(@Nonnull K key) throws Exception {
            if (removing.contains(key)) {
                return null;
            }
            return dataAccessor.get(key, clazz);
        }
    }

    private class DbRemovalListener implements RemovalListener<K, V> {

        @Override
        public void onRemoval(@Nonnull RemovalNotification<K, V> notification) {
            if (notification.getCause() == RemovalCause.EXPIRED) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Remove entry from Cache(not the DB): " + notification);
                }
                return;
            }

            K id = notification.getKey();
            V value = notification.getValue();
            if (id == null || value == null) {
                return;
            }

            removing.add(id);
            persistenceProcessor.delete(value, () -> removing.remove(id));
        }
    }
}
