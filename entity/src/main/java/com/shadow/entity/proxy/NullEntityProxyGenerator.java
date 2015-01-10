package com.shadow.entity.proxy;

import com.shadow.entity.IEntity;

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 * @author nevermore on 2014/11/26.
 */
public class NullEntityProxyGenerator<PK extends Serializable, T extends IEntity<PK>> implements EntityProxyGenerator<PK, T> {

    @Nonnull
    @Override
    public T generate(T entity) {
        return entity;
    }
}
