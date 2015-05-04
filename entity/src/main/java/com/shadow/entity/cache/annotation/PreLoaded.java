package com.shadow.entity.cache.annotation;


import com.shadow.entity.IEntity;
import com.shadow.entity.db.Repository;

import javax.annotation.Nonnull;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collections;
import java.util.List;

/**
 * 预加载
 *
 * @author nevermore on 2014/11/26.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PreLoaded {

    Policy policy();

    enum Policy {
        QUERY {
            @Nonnull
            @Override
            public <T extends IEntity<?>> List<T> load(Repository repository, Class<T> clazz) {
//                return repository.namedQuery(clazz.getSimpleName() + ".init");
                return Collections.emptyList();
            }
        },
        ALL {
            @Nonnull
            @Override
            public <T extends IEntity<?>> List<T> load(Repository dataAccessor, Class<T> clazz) {
//                return repository.query(clazz);
                return Collections.emptyList();
            }
        };

        @Nonnull
        public abstract <T extends IEntity<?>> List<T> load(Repository dataAccessor, Class<T> clazz);
    }
}
