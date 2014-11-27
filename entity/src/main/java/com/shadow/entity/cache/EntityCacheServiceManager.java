package com.shadow.entity.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.shadow.entity.IEntity;
import com.shadow.entity.orm.DataAccessor;
import com.shadow.entity.orm.persistence.PersistenceEventHandler;
import com.shadow.entity.orm.persistence.PersistenceProcessor;
import com.shadow.entity.orm.persistence.QueuedPersistenceProcessor;

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 * 实体缓存服务管理器
 *
 * @author nevermore on 2014/11/26.
 */
public final class EntityCacheServiceManager<K extends Serializable, V extends IEntity<K>> {

    private final DataAccessor dataAccessor;
    private final PersistenceEventHandler persistenceEventHandler;
    private LoadingCache<Class<V>, PersistenceProcessor<V>> persistenceProcessors;
    private LoadingCache<Class<V>, EntityCacheService<K, V>> cacheServices;

    public EntityCacheServiceManager(DataAccessor dataAccessor, PersistenceEventHandler persistenceEventHandler) {
        this.dataAccessor = dataAccessor;
        this.persistenceEventHandler = persistenceEventHandler;

        initialize();
    }

    private void initialize() {
        persistenceProcessors = CacheBuilder.newBuilder().build(new CacheLoader<Class<V>, PersistenceProcessor<V>>() {
            @Override
            public PersistenceProcessor<V> load(@Nonnull Class<V> clazz) throws Exception {
                return new QueuedPersistenceProcessor<>(persistenceEventHandler);
            }
        });

        cacheServices = CacheBuilder.newBuilder().build(new CacheLoader<Class<V>, EntityCacheService<K, V>>() {
            @Override
            public EntityCacheService<K, V> load(@Nonnull Class<V> clazz) throws Exception {
                return new RamEntityCacheService<>(clazz, dataAccessor, persistenceProcessors.get(clazz));
            }
        });
    }

    public EntityCacheService<K, V> getCacheService(@Nonnull Class<V> clazz) {
        return cacheServices.getUnchecked(clazz);
    }
}
