package com.shadow.entity.cache.annotation;

import java.lang.annotation.*;

/**
 * 可缓存的实体类注解
 *
 * @author nevermore on 2014/11/26.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface Cacheable {

    /**
     * 容量系数
     *
     * @return
     */
    public CacheSizeFactor sizeFactor() default CacheSizeFactor.NORMAL;

    /**
     * 并发级别
     *
     * @return
     */
    public int concurrencyLevel() default 16;

    /**
     * 读操作后存活时间
     *
     * @return
     */
    public String expireAfterAccess() default "30m";


    /**
     * 写操作后存活时间
     *
     * @return
     */
    public String expireAfterWrite() default "";

    /**
     * 是否记录缓存统计数据
     *
     * @return
     */
    public boolean recordStats() default true;

    /**
     * 缓存key是否为弱引用
     *
     * @return
     */
    public boolean weakKeys() default false;

    /**
     * 缓存容量系数
     */
    public enum CacheSizeFactor {
        /**
         * 最小容量(16)
         */
        MINIMUM {
            @Override
            public int getSize(int size) {
                return 16;
            }
        },
        /**
         * 配置的默认容量
         */
        NORMAL,
        /**
         * 默认容量2倍
         */
        DOUBLE,
        /**
         * 默认容量的3倍
         */
        TRIPLE;

        public int getSize(int size) {
            return this.ordinal() * size;
        }
    }
}
