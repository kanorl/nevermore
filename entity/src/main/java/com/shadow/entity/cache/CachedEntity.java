package com.shadow.entity.cache;

import com.shadow.entity.IEntity;

/**
 * @author nevermore on 2015/1/16.
 */
public interface CachedEntity {

    <T extends IEntity<?>> T getEntity();

    Object getIndexValue();

    long postEdit();

    boolean isPersisted();

    void postPersist();
}
