package com.shadow.entity.id;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Range;
import com.shadow.common.config.ServerConfig;
import com.shadow.entity.IEntity;
import com.shadow.entity.orm.DataAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author nevermore on 2015/1/10
 */
@Component
public class EntityIdGenerator {

    @Autowired
    private ServerConfig serverConfig;
    @Autowired
    private DataAccessor dataAccessor;

    private final LoadingCache<Class<? extends IEntity<Long>>, LoadingCache<Short, LoadingCache<Short, AtomicLong>>> cache = CacheBuilder.newBuilder().concurrencyLevel(16).build(
            new CacheLoader<Class<? extends IEntity<Long>>, LoadingCache<Short, LoadingCache<Short, AtomicLong>>>() {
                @Override
                public LoadingCache<Short, LoadingCache<Short, AtomicLong>> load(@Nonnull Class<? extends IEntity<Long>> entityClass) throws Exception {
                    return CacheBuilder.newBuilder().build(new CacheLoader<Short, LoadingCache<Short, AtomicLong>>() {
                        @Override
                        public LoadingCache<Short, AtomicLong> load(@Nonnull Short platform) throws Exception {
                            return CacheBuilder.newBuilder().build(new CacheLoader<Short, AtomicLong>() {
                                @Override
                                public AtomicLong load(@Nonnull Short server) throws Exception {
                                    Range<Long> range = EntityIdRule.idRange(platform, server);
                                    long currentMaxId = dataAccessor.queryMaxId(entityClass, range).orElse(range.lowerEndpoint());
                                    return new AtomicLong(currentMaxId);
                                }
                            });
                        }
                    });
                }
            }
    );

    @PostConstruct
    private void init() {
        Range<Short> range = EntityIdRule.platformRange();
        for (Short platform : serverConfig.getPlatforms()) {
            Preconditions.checkState(range.contains(platform), "平台标识超出范围: platform=%s, range=%s", platform, range);
        }
    }

    public long next(@Nonnull Class<? extends IEntity<Long>> entityClass, short platform, short server) {
        long id = cache.getUnchecked(entityClass).getUnchecked(platform).getUnchecked(server).incrementAndGet();
        Range<Long> range = EntityIdRule.idRange(platform, server);
        if (!range.contains(id)) {
            throw new IllegalStateException("ID超出范围: class= " + entityClass.getName() + ", server= " + server + ", id=" + id + ", range=" + range);
        }
        return id;
    }

    public long next(@Nonnull Class<? extends IEntity<Long>> entityClass, long ownerId) {
        return next(entityClass, EntityIdRule.platform(ownerId), EntityIdRule.server(ownerId));
    }
}
