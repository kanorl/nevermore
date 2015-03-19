package com.shadow.entity.orm.persistence;

/**
 * 入库策略
 *
 * @author nevermore on 2015/3/19
 */
public enum PersistencePolicy {

    /**
     * 立即
     */
    IMMEDIATELY,

    /**
     * 定时
     */
    SCHEDULED
}
