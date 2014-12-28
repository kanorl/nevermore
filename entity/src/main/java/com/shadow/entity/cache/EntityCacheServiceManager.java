package com.shadow.entity.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.shadow.entity.IEntity;
import com.shadow.entity.orm.DataAccessor;
import com.shadow.entity.orm.persistence.PersistenceEventHandler;
import com.shadow.entity.orm.persistence.PersistenceProcessor;
import com.shadow.entity.orm.persistence.QueuedPersistenceProcessor;
import com.shadow.util.thread.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 * 实体缓存服务管理器
 *
 * @author nevermore on 2014/11/26.
 */
public final class EntityCacheServiceManager<K extends Serializable, V extends IEntity<K>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntityCacheServiceManager.class);

    private final DataAccessor dataAccessor;
    private final PersistenceEventHandler persistenceEventHandler;
    private int defaultCacheSize;
    private LoadingCache<Class<V>, PersistenceProcessor<V>> persistenceProcessors;
    private LoadingCache<Class<V>, EntityCacheService<K, V>> cacheServices;

    public EntityCacheServiceManager(DataAccessor dataAccessor, PersistenceEventHandler persistenceEventHandler, int defaultCacheSize) {
        this.dataAccessor = dataAccessor;
        this.persistenceEventHandler = persistenceEventHandler;
        this.defaultCacheSize = defaultCacheSize;
        initialize();
    }

    private void initialize() {
        persistenceProcessors = CacheBuilder.newBuilder().build(new CacheLoader<Class<V>, PersistenceProcessor<V>>() {
            @Override
            public PersistenceProcessor<V> load(@Nonnull Class<V> clazz) throws Exception {
                return new QueuedPersistenceProcessor<>(persistenceEventHandler, new NamedThreadFactory(clazz.getSimpleName() + "持久化线程"));
            }
        });

        cacheServices = CacheBuilder.newBuilder().build(new CacheLoader<Class<V>, EntityCacheService<K, V>>() {
            @Override
            public EntityCacheService<K, V> load(@Nonnull Class<V> clazz) throws Exception {
                return new RamEntityCacheService<>(clazz, dataAccessor, persistenceProcessors.get(clazz), defaultCacheSize);
            }
        });
    }

    public void shutdown() {
        LOGGER.error("开始关闭持久化处理器...");
        persistenceProcessors.asMap().forEach((clazz, processor) -> {
            processor.shutdown();
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("实体类 {} 持久化处理器已关闭...", clazz.getSimpleName());
            }
        });
        LOGGER.error("完成关闭持久化处理器...");
    }

    public EntityCacheService<K, V> getCacheService(@Nonnull Class<V> clazz) {
        return cacheServices.getUnchecked(clazz);
    }

    public PersistenceProcessor<V> getPersistenceProcessor(@Nonnull Class<V> clazz) {
        return persistenceProcessors.getIfPresent(clazz);
    }
}
