package com.shadow.entity;

import java.io.Serializable;

/**
 * @author nevermore on 2014/11/26
 */
public interface IEntity<K extends Serializable> {

    K getId();
}
