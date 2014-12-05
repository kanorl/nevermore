package com.shadow.entity.lock;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.shadow.entity.IEntity;
import com.shadow.entity.lock.exception.IllegalLockTargetException;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * 基于稳定排序的对象锁链
 *
 * @author nevermore on 2014/11/27.
 */
public class LockChain {
    private static final Logger LOGGER = LoggerFactory.getLogger(LockChain.class);

    private static final LoadingCache<Object, ObjectLock> LOCK_CACHE = CacheBuilder.newBuilder().concurrencyLevel(16).expireAfterAccess(10, TimeUnit.MINUTES).build(new CacheLoader<Object, ObjectLock>() {
        @Override
        public ObjectLock load(@Nonnull Object key) throws Exception {
            return new ObjectLock(key);
        }
    });

    private final ObjectLock[] locks;

    private LockChain(ObjectLock[] locks) {
        this.locks = locks;
    }

    public static LockChain build(@Nonnull Object... objects) {
        if (ArrayUtils.isEmpty(objects)) {
            throw new IllegalArgumentException("找不到加锁对象。");
        }
        ObjectLock[] locks = new ObjectLock[objects.length];
        for (int i = 0; i < objects.length; i++) {
            Object obj = objects[i];
            if (obj == null) {
                throw new IllegalLockTargetException("加锁对象不能为null。");
            }
            locks[i] = LOCK_CACHE.getUnchecked(obj);
        }

        Arrays.sort(locks);

        return new LockChain(locks);
    }

    public void lock() {
        for (ObjectLock lock : locks) {
            lock.lock();

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(lock.toString());
            }
        }
    }

    public void unlock() {
        for (int i = locks.length - 1; i >= 0; i--) {
            ObjectLock lock = locks[i];
            lock.unlock();

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(lock.toString());
            }
        }
    }

    /**
     * 判断指定对象的锁是否被当前线程持有(只适用于判断使用{@link com.shadow.entity.lock.LockChain}方式加的锁)
     *
     * @param o Object
     * @return true - 该对象的锁被当前线程持有 false - 该对象的锁未被当前线程持有(如果o==null，则始终返回false)
     * @see Thread#holdsLock(Object)
     * @see java.util.concurrent.locks.ReentrantLock#isHeldByCurrentThread()
     */
    public static boolean isLockedByCurrentThread(Object o) {
        if (o == null) {
            return false;
        }
        ObjectLock objectLock = LOCK_CACHE.getIfPresent(o);
        return objectLock != null && objectLock.isHeldByCurrentThread();
    }

    @Override
    public String toString() {
        return Arrays.toString(locks);
    }

    public static void main(String[] args) {
        isLockedByCurrentThread(1);
        IEntity<Integer> o1 = () -> 10;
        IEntity<String> o2 = () -> "id";
        Object o3 = new ArrayList<>();

        Thread t1 = new Thread(() -> {
            LockChain lockChain = LockChain.build(o3, o2, o1);
            lockChain.lock();
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lockChain.unlock();
            }
        });

        Thread t2 = new Thread(() -> {
            LockChain lockChain = LockChain.build(o2, o1, o3);
            lockChain.lock();
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lockChain.unlock();
            }
        });

        t1.start();
        t2.start();
    }
}
