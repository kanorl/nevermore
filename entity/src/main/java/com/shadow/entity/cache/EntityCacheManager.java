package com.shadow.entity.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.shadow.entity.IEntity;
import com.shadow.entity.annotation.CacheIndex;
import com.shadow.entity.annotation.CacheSize;
import com.shadow.entity.orm.DataAccessor;
import com.shadow.entity.orm.persistence.PersistenceProcessor;
import com.shadow.entity.orm.persistence.QueuedPersistenceProcessor;
import org.reflections.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import java.io.Serializable;

/**
 * 实体缓存服务管理器
 *
 * @author nevermore on 2014/11/26.
 */
public final class EntityCacheManager<K extends Serializable, V extends IEntity<K>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntityCacheManager.class);

    @Autowired
    private DataAccessor dataAccessor;
    @Value("${server.cache.size.minimum}")
    private int minimumCacheSize;
    @Value("${server.cache.size.default}")
    private int defaultCacheSize;
    @Value("${server.persistence.pool.size:1}")
    private int persistencePoolSize;

    private LoadingCache<Class<V>, PersistenceProcessor<V>> persistenceProcessors;
    private LoadingCache<Class<V>, EntityCache<K, V>> entityCaches;

    @PostConstruct
    private void init() {
        CacheSize.Size.MINIMUM.set(minimumCacheSize);
        CacheSize.Size.DEFAULT.set(defaultCacheSize);

        persistenceProcessors = CacheBuilder.newBuilder().build(new CacheLoader<Class<V>, PersistenceProcessor<V>>() {
            @Override
            public PersistenceProcessor<V> load(@Nonnull Class<V> clazz) throws Exception {
                return new QueuedPersistenceProcessor<>(dataAccessor, clazz.getSimpleName(), persistencePoolSize);
            }
        });

        entityCaches = CacheBuilder.newBuilder().build(new CacheLoader<Class<V>, EntityCache<K, V>>() {
            @SuppressWarnings("unchecked")
            @Override
            public EntityCache<K, V> load(@Nonnull Class<V> clazz) throws Exception {
                return ReflectionUtils.getAllFields(clazz, field -> field.isAnnotationPresent(CacheIndex.class)).isEmpty() ? new DefaultEntityCache<>(clazz, dataAccessor, persistenceProcessors.get(clazz)) : new DefaultRegionEntityCache<>(clazz, dataAccessor, persistenceProcessors.get(clazz));
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
}
