package com.shadow.entity.cache.annotation;

import com.shadow.entity.orm.persistence.PersistencePolicy;

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
     * 最大容量
     *
     * @return
     */
    CacheSize cacheSize() default @CacheSize;

    /**
     * 入库策略
     *
     * @return
     */
    PersistencePolicy persistencePolicy() default PersistencePolicy.SCHEDULED;

    /**
     * 并发级别
     *
     * @return
     */
    int concurrencyLevel() default 16;

    /**
     * 读操作后存活时间
     *
     * @return
     */
    String expireAfterAccess() default "30m";


    /**
     * 写操作后存活时间
     *
     * @return
     */
    String expireAfterWrite() default "";

    /**
     * 是否记录缓存统计数据
     *
     * @return
     */
    boolean recordStats() default true;

    /**
     * 缓存key是否为弱引用
     *
     * @return
     */
    boolean weakKeys() default false;
}
