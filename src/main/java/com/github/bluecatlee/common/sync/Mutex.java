package com.github.bluecatlee.common.sync;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 自定义独占锁的简单实现
 * Created by 胶布 on 2020/3/18.
 */
public class Mutex implements Lock {

    /**
     * 自定义同步器
     */
    private static final class Sync extends AbstractQueuedSynchronizer {

        /**
         * 返回此同步器是否在独占模式下被占用
         */
        protected boolean isHeldExclusively() {
            return getState() == 1;
        }

        /**
         * 独占式获取同步状态 不可重入 非公平锁
         */
        protected boolean tryAcquire(int acquires) {
            if (compareAndSetState(0,1)) {
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }

        /**
         * 独占式获取同步状态 可重入 公平锁
         */
        protected boolean tryAcquire1(int acquires) {
            final Thread current = Thread.currentThread();
            int c = getState();
            if (c == 0) {
                if (!hasQueuedPredecessors() && compareAndSetState(0, acquires)) {
                    setExclusiveOwnerThread(current);
                    return true;
                }
            } else if (current == getExclusiveOwnerThread()) {
                int nextc = c + acquires;
                if (nextc < 0) {
                    throw new Error("Maximum lock count exceeded");
                }
                setState(nextc);
                return true;
            }
            return false;
        }

        /**
         * 独占式释放同步状态
         */
        protected boolean tryRelease(int release) {
            if (getState() == 0) {
                throw new IllegalMonitorStateException();
            }
            setExclusiveOwnerThread(null);
            setState(0);
            return true;
        }

        protected boolean tryRelease1(int release) {
            int c = getState() - release;
            if (Thread.currentThread() != getExclusiveOwnerThread()) {
                throw new IllegalMonitorStateException();
            }
            boolean free = false;
            if (c == 0) {
                free = true;
                setExclusiveOwnerThread(null);
            }
            setState(c);
            return free;
        }

        Condition newCondition() {
            return new ConditionObject();
        }

    }


    /*
     * 将锁的获取释放等操作代理到Sync
     *
     */

    private final Sync sync = new Sync();

    /**
     * 获取锁
     */
    @Override
    public void lock() {
        sync.acquire(1);
    }

    /**
     * 释放锁
     */
    @Override
    public void unlock() {
        sync.release(1);
    }

    /**
     * 判断当前线程是否获得了独占锁
     */
    public boolean isLocked() {
        return sync.isHeldExclusively();
    }

    /**
     * 判断当前锁是否有线程在等待
     */
    public boolean hasQueuedThreads() {
        return sync.hasQueuedThreads();
    }

    /**
     * 获取可中断的锁
     * @throws InterruptedException
     */
    @Override
    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);
    }

    @Override
    public boolean tryLock() {
        return sync.tryAcquire(1);
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireNanos(1, unit.toNanos(time));
    }

    /**
     * 返回一个绑定在当前锁上的条件
     */
    @Override
    public Condition newCondition() {
        return sync.newCondition();
    }

}
