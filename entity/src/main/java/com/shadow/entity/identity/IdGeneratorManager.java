package com.shadow.entity.identity;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.shadow.entity.IEntity;
import com.shadow.entity.orm.DataAccessor;
import com.shadow.util.config.ServerProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author nevermore on 2015/1/10
 */
@Component
public class IdGeneratorManager {

    @Autowired
    private ServerProperty serverProperty;
    @Autowired
    private DataAccessor dataAccessor;

    private final LoadingCache<Class<? extends IEntity<Long>>, LoadingCache<Short, IdGenerator>> cache = CacheBuilder.newBuilder().build(new CacheLoader<Class<? extends IEntity<Long>>, LoadingCache<Short, IdGenerator>>() {
        @Override
        public LoadingCache<Short, IdGenerator> load(Class<? extends IEntity<Long>> key) throws Exception {
            return CacheBuilder.newBuilder().build(new CacheLoader<Short, IdGenerator>() {
                @Override
                public IdGenerator load(Short server) throws Exception {
                    IdRange range = IdRule.range(serverProperty.getPlatform(), server);
                    long currentMaxId = dataAccessor.queryMaxId(key, range).orElse(range.getMin());
                    return new IdGenerator(serverProperty.getPlatform(), server, currentMaxId);
                }
            });
        }
    });

    public long get(Class<? extends IEntity<Long>> entityClass, short server) {
        return cache.getUnchecked(entityClass).getUnchecked(server).next();
    }

    public long get(Class<? extends IEntity<Long>> entityClass, long ownerId) {
        return get(entityClass, IdRule.server(ownerId));
    }
}
