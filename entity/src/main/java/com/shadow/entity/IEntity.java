package com.shadow.entity;

import com.shadow.entity.cache.annotation.Cacheable;

import java.io.Serializable;

/**
 * 实体类接口
 *
 * @author nevermore on 2014/11/26
 */
@Cacheable
public interface IEntity<K extends Serializable> {

    K getId();
}
