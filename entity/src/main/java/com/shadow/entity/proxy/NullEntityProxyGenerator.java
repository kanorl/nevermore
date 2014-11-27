package com.shadow.entity.proxy;

import com.shadow.entity.IEntity;

import java.io.Serializable;

/**
 * @author nevermore on 2014/11/26.
 */
public class NullEntityProxyGenerator<PK extends Serializable, T extends IEntity<PK>> extends EntityProxyGenerator<PK, T> {

    public NullEntityProxyGenerator() {
        super(null);
    }

    @Override
    public T generate(T entity) throws Exception {
        return entity;
    }
}
