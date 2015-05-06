package com.shadow.entity.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.shadow.common.util.lang.ReflectUtil;
import com.shadow.entity.IEntity;
import com.shadow.entity.cache.annotation.CacheIndex;
import com.shadow.entity.cache.annotation.CacheSize;
import com.shadow.entity.cache.annotation.Cacheable;
import com.shadow.entity.db.Repository;
import com.shadow.entity.db.persistence.PersistencePolicy;
import com.shadow.entity.db.persistence.PersistenceProcessor;
import com.shadow.entity.db.persistence.QueuedPersistenceProcessor;
import com.shadow.entity.db.persistence.ScheduledPersistenceProcessor;
import org.reflections.ReflectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import java.io.Serializable;

/**
 * 实体缓存服务管理器
 *
 * @author nevermore on 2014/11/26.
 */
@SuppressWarnings("unchecked")
public final class EntityCacheManager {

    @Autowired
    private Repository repository;
    @Value("${server.cache.size.minimum}")
    private int minimumCacheSize;
    @Value("${server.cache.size.default}")
    private int defaultCacheSize;
    @Autowired
    private ApplicationContext ctx;

    private LoadingCache<Class<? extends IEntity<?>>, EntityCache<?, ? extends IEntity<?>>> entityCaches;

    @PostConstruct
    private void init() {
        CacheSize.Size.MINIMUM.set(minimumCacheSize);
        CacheSize.Size.DEFAULT.set(defaultCacheSize);

        entityCaches = CacheBuilder.newBuilder().build(new CacheLoader<Class<? extends IEntity<?>>, EntityCache<?, ? extends IEntity<?>>>() {
            @Override
            public EntityCache<?, ? extends IEntity<?>> load(@Nonnull Class<? extends IEntity<?>> clazz) throws
                    Exception {
                if (ReflectionUtils.getAllFields(clazz, field -> field.isAnnotationPresent(CacheIndex.class)).isEmpty()) {
                    return new DefaultEntityCache<>(clazz, repository, getPersistenceProcessor(clazz));
                }
                return new DefaultRegionEntityCache<>(clazz, repository, getPersistenceProcessor(clazz));
            }
        });
    }

    private PersistenceProcessor<?> getPersistenceProcessor(Class<? extends IEntity<?>> entityType) {
        Cacheable cacheable = ReflectUtil.getDeclaredAnnotation(entityType, Cacheable.class);
        if (cacheable == null) {
            throw new IllegalStateException("@Cacheable not found in " + entityType.getSimpleName());
        }
        return cacheable.persistencePolicy() == PersistencePolicy.SCHEDULED ?
                ctx.getBean(ScheduledPersistenceProcessor.class)
                : ctx.getBean(QueuedPersistenceProcessor.class);
    }

    @Nonnull
    public <K extends Serializable, V extends IEntity<K>> EntityCache<K, V> getEntityCache(@Nonnull Class<? extends IEntity<?>> clazz) {
        return (EntityCache<K, V>) entityCaches.getUnchecked(clazz);
    }
}
