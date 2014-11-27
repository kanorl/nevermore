package com.shadow.entity.lock.annotation;


import com.shadow.entity.lock.IllegalLockTargetException;

import javax.annotation.Nonnull;
import java.lang.annotation.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author nevermore on 2014/11/27.
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface LockTarget {

    public Type value() default Type.Object;

    public enum Type {
        Object {
            @Override
            public Collection<?> extract(@Nonnull Object arg) throws IllegalLockTargetException {
                return Collections.singletonList(checkNotNull(arg));
            }
        },
        Element {
            @Override
            public Collection<?> extract(@Nonnull Object arg) throws IllegalLockTargetException {
                checkNotNull(arg);

                if (arg instanceof Collection<?>) {
                    Collection<?> c = (Collection<?>) arg;
                    List<Object> lockObjects = new ArrayList<>(c.size());
                    for (Object o : c) {
                        if (o == null) {
                            throw new IllegalLockTargetException("Arg annotated by @LockTarget must not contain null elements.");
                        }
                        lockObjects.add(o);
                    }
                    return lockObjects;
                }

                if (arg.getClass().isArray()) {
                    Object[] array = (Object[]) arg;
                    List<Object> lockObjects = new ArrayList<>(array.length);
                    for (Object o : array) {
                        if (o == null) {
                            throw new IllegalLockTargetException("Arg annotated by @LockTarget must not contain null elements.");
                        }
                        lockObjects.add(arg);
                    }
                    return lockObjects;
                }

                throw new IllegalLockTargetException("Unsupported lock target type.(This should never be happened)");
            }
        };

        public abstract Collection<?> extract(@Nonnull Object arg) throws IllegalLockTargetException;

        protected <T> T checkNotNull(T t) throws IllegalLockTargetException {
            if (t == null) {
                throw new IllegalLockTargetException("Arg annotated by @LockTarget cannot be null.");
            }
            return t;
        }
    }
}
