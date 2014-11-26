package com.shadow.entity;

/**
 * 实体类工厂
 *
 * @author nevermore on 2014/11/26.
 */
@FunctionalInterface
public interface EntityFactory<T> {

    T newInstance();
}
