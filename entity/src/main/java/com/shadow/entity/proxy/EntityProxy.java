package com.shadow.entity.proxy;


import com.shadow.entity.IEntity;

/**
 * 实体代理类专用接口
 *
 * @author nevermore on 2014/11/26.
 */
@FunctionalInterface
public interface EntityProxy {
    <T extends IEntity<?>> T getEntity();
}
