package com.shadow.entity.cache.annotation;


import com.shadow.entity.IEntity;
import com.shadow.entity.db.Repository;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
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

    String where() default "";

    enum Policy {
        QUERY {
            @Nonnull
            @Override
            public <T extends IEntity<?>> List<T> load(Repository repository, Class<T> clazz) {
                PreLoaded preLoaded = clazz.getAnnotation(PreLoaded.class);
                if (preLoaded == null) {
                    throw new IllegalStateException("@PreLoaded not found in " + clazz.getSimpleName());
                }
                if (StringUtils.isEmpty(preLoaded.where())) {
                    throw new IllegalStateException("@PreLoaded.where is required if @PreLoaded.policy==QUERY in " + clazz.getSimpleName());
                }
                return repository.query(clazz, preLoaded.where());
            }
        },
        ALL {
            @Nonnull
            @Override
            public <T extends IEntity<?>> List<T> load(Repository repository, Class<T> clazz) {
                return repository.getAll(clazz);
            }
        };

        @Nonnull
        public abstract <T extends IEntity<?>> List<T> load(Repository repository, Class<T> clazz);
    }
}
