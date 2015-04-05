package com.shadow.entity.cache.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 只作用于IEntity的子类
 *
 * @author nevermore on 2014/11/26.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AutoSave {

    Result forResult() default Result.NULL;

    boolean withIndexValueChanged() default false;

    enum Result {

        NULL(""),

        TRUE("true"),

        FALSE("false");

        private String value;

        Result(String value) {
            this.value = value;
        }

        public boolean isNull() {
            return this == NULL;
        }

        @Override
        public String toString() {
            return this.value;
        }
    }
}
