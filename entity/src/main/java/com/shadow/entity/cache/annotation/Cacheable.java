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
     * 初始化容量
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
     * 是否记录缓存状态
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

    public enum CacheSizeFactor {
        MINIMUM {
            @Override
            public int getSize(int size) {
                return 16;
            }
        },
        NORMAL,
        DOUBLE,
        TRIPLE;

        public int getSize(int size) {
            return this.ordinal() * size;
        }
    }
}