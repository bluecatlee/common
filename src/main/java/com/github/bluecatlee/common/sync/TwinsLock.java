package com.github.bluecatlee.common.sync;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 自定义一个最多两个线程同时访问的共享锁
 * Created by 胶布 on 2020/3/18.
 */
public class TwinsLock implements Lock {

    private final Sync sync = new Sync(2);

    /**
     * 自定义同步器
     */
    private static final class Sync extends AbstractQueuedSynchronizer {
        Sync (int count) {
            if (count <= 0) {
                throw new IllegalArgumentException();
            }
            setState(count);
        }

        protected int tryAcquireShared(int reduceCount) {
            for (;;) {
                int current = getState();
                int newCount = current - reduceCount;
                if (newCount < 0 || compareAndSetState(current, newCount)) {
                    return newCount;
                }
            }
        }

        protected boolean tryReleaseShared(int returnCount) {
            for (;;) {
                int current = getState();
                int newCount = current + returnCount;
                if (compareAndSetState(current, newCount)) {
                    return true;
                }
            }
        }

        Condition newCondition() {
            return new ConditionObject();
        }

    }

    @Override
    public void lock() {
        sync.acquireShared(1);
    }

    @Override
    public void unlock() {
        sync.releaseShared(1);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        sync.acquireSharedInterruptibly(1);
    }

    @Override
    public boolean tryLock() {
        return sync.tryAcquireShared(1) > 0;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireSharedNanos(1, unit.toNanos(time));
    }

    @Override
    public Condition newCondition() {
        return sync.newCondition();
    }

    /**
     * 判断当前锁是否有线程在等待
     */
    public boolean hasQueuedThreads() {
        return sync.hasQueuedThreads();
    }

    public List<Thread> getQueuedThreads() {
        return (List)sync.getQueuedThreads();
    }

}
