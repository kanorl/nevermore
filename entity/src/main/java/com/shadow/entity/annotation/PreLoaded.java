package com.shadow.entity.annotation;


import com.shadow.entity.IEntity;
import com.shadow.entity.orm.DataAccessor;

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

    public Type type();

    public String queryName() default "";

    public enum Type {
        NAMED_QUERY {
            @Override
            public <T extends IEntity<?>> List<T> load(DataAccessor dataAccessor, String queryName, Class<T> clazz) {
                return dataAccessor.namedQuery(clazz, queryName);
            }
        },
        ALL {
            @Override
            public <T extends IEntity<?>> List<T> load(DataAccessor dataAccessor, String queryName, Class<T> clazz) {
                return dataAccessor.getAll(clazz);
            }
        };

        public abstract <T extends IEntity<?>> List<T> load(DataAccessor dataAccessor, String queryName, Class<T> clazz);
    }
}
