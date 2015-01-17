package com.shadow.entity.proxy;

import com.shadow.entity.IEntity;

/**
 * @author nevermore on 2015/1/16.
 */
public interface VersionedEntityProxy {

    <T extends IEntity<?>> T getEntity();

    long postEdit();

    boolean isPersisted();

    void postPersist();
}
