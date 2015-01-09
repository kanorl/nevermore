package com.shadow.entity;


import com.shadow.entity.annotation.Cacheable;

import java.io.Serializable;

/**
 * 可缓存的实体类
 *
 * @author nevermore on 2014/11/26.
 */
@Cacheable
public abstract class CacheableEntity<K extends Serializable> implements IEntity<K> {
}
