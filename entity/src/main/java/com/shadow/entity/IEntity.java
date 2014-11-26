package com.shadow.entity;

import java.io.Serializable;

/**
 * 实体类接口
 *
 * @author nevermore on 2014/11/26
 */
public interface IEntity<K extends Serializable> {

    K getId();
}
