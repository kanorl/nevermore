package com.shadow.entity.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.shadow.entity.IEntity;
import com.shadow.entity.orm.DataAccessor;
import com.shadow.entity.orm.persistence.PersistenceProcessor;
import com.shadow.entity.orm.persistence.QueuedPersistenceProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;

/**
 * 实体缓存服务管理器
 *
 * @author nevermore on 2014/11/26.
 */
public final class EntityCacheManager<K extends Serializable, V extends IEntity<K>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntityCacheManager.class);

    private final DataAccessor dataAccessor;
    private int persistencePoolSize;
    private LoadingCache<Class<V>, PersistenceProcessor<V>> persistenceProcessors;
    private LoadingCache<Class<V>, EntityCache<K, V>> entityCaches;
    private LoadingCache<Class<V>, RegionEntityCache<K, V>> regionEntityCaches;

    public EntityCacheManager(DataAccessor dataAccessor, int persistencePoolSize) {
        this.dataAccessor = dataAccessor;
        this.persistencePoolSize = persistencePoolSize;
        initialize();
    }

    private void initialize() {
        persistenceProcessors = CacheBuilder.newBuilder().build(new CacheLoader<Class<V>, PersistenceProcessor<V>>() {
            @Override
            public PersistenceProcessor<V> load(@Nonnull Class<V> clazz) throws Exception {
                return new QueuedPersistenceProcessor<>(dataAccessor, clazz.getSimpleName(), persistencePoolSize);
            }
        });

        entityCaches = CacheBuilder.newBuilder().build(new CacheLoader<Class<V>, EntityCache<K, V>>() {
            @Override
            public EntityCache<K, V> load(@Nonnull Class<V> clazz) throws Exception {
                return new EntityCacheImpl<>(clazz, dataAccessor, persistenceProcessors.get(clazz));
            }
        });
        regionEntityCaches = CacheBuilder.newBuilder().build(new CacheLoader<Class<V>, RegionEntityCache<K, V>>() {
            @Override
            public RegionEntityCache<K, V> load(@Nonnull Class<V> clazz) throws Exception {
                return new RegionEntityCacheImpl<>(clazz, dataAccessor, persistenceProcessors.get(clazz));
            }
        });
    }

    public void shutdown() {
        LOGGER.error("开始[关闭持久化处理器]");
        persistenceProcessors.asMap().values().forEach(PersistenceProcessor::shutdown);
        LOGGER.error("完成[关闭持久化处理器]");
    }

    @Nonnull
    public EntityCache<K, V> getEntityCache(@Nonnull Class<V> clazz) {
        return entityCaches.getUnchecked(clazz);
    }

    @Nonnull
    public RegionEntityCache<K, V> getRegionEntityCache(@Nonnull Class<V> clazz) {
        return regionEntityCaches.getUnchecked(clazz);
    }


    @Nullable
    public PersistenceProcessor<V> getPersistenceProcessor(@Nonnull Class<V> clazz) {
        return persistenceProcessors.getIfPresent(clazz);
    }
}
