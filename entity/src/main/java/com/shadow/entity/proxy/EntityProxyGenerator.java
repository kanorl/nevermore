package com.shadow.entity.proxy;

import com.shadow.entity.IEntity;

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 * @author nevermore on 2015/1/10
 */
public interface EntityProxyGenerator<PK extends Serializable, T extends IEntity<PK>> {

    @Nonnull
    T generate(T entity);
}
