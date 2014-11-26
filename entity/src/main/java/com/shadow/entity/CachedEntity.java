package com.shadow.entity;


import com.shadow.entity.cache.annotation.Cached;

import java.io.Serializable;

/**
 * 可缓存的实体类
 *
 * @author nevermore on 2014/11/26.
 */
@Cached
public abstract class CachedEntity<K extends Serializable> implements com.shadow.entity.IEntity<K> {
}
