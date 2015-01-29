package com.shadow.entity.proxy;

import com.shadow.entity.IEntity;

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 * @author nevermore on 2015/1/10
 */
public interface EntityProxyTransformer<PK extends Serializable, T extends IEntity<PK>> {

    @Nonnull
    T transform(T entity);
}
