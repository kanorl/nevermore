package com.shadow.entity.lock;


import com.shadow.entity.IEntity;

import javax.annotation.Nonnull;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 可重入对象锁
 *
 * @author nevermore on 2014/11/27.
 */
class ObjectLock extends ReentrantLock implements Comparable<ObjectLock> {

    private Integer weight;
    private boolean isEntity;
    private Class<?> objClass;

    ObjectLock(Object o) {
        this(o, false);
    }

    ObjectLock(Object o, boolean fair) {
        super(fair);

        objClass = o.getClass();
        isEntity = (o instanceof IEntity);
        weight = isEntity ? ((IEntity<?>) o).getId().hashCode() : System.identityHashCode(o);
    }

    @Override
    public int compareTo(@Nonnull ObjectLock other) {
        /*通常情况下，Entity对象的锁的竞争性要比非Entity对象的锁的竞争性要小得多
         *所以优先获取非Entity对象的锁可减少其他只需要获取Entity对象的锁的线程的等待时间
         */
        if (this.isEntity && !other.isEntity) {
            return 1;
        }
        if (!this.isEntity && other.isEntity) {
            return -1;
        }

        if (this.objClass != other.objClass) {
            if (this.objClass.hashCode() == other.objClass.hashCode()) {
                return this.objClass.getName().compareTo(other.objClass.getName());
            } else {
                return this.objClass.hashCode() > other.objClass.hashCode() ? 1 : -1;
            }
        }

        return this.weight.compareTo(other.weight);
    }

    @Override
    public String toString() {
        return super.toString() + ": [" + objClass + ", " + isEntity + ", " + weight + "]";
    }
}
