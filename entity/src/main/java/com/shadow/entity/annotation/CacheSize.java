package com.shadow.entity.annotation;

import com.google.common.base.Preconditions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 缓存容量
 * maximumSize = size * factor
 *
 * @author nevermore on 2015/1/3.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CacheSize {

    /**
     * 容量
     *
     * @return
     */
    public Size size() default Size.DEFAULT;

    /**
     * 容量系数
     *
     * @return
     */
    public double factor() default 1;


    /**
     * 缓存容量系数
     */
    public enum Size {
        /**
         * 最小容量
         */
        MINIMUM,

        /**
         * 默认容量
         */
        DEFAULT;

        private int value;

        public void set(int value) {
            Preconditions.checkState(this.value == 0, this + "容量的大小已经设置为" + this.value);
            this.value = value;
        }

        public int get() {
            return value;
        }
    }
}
