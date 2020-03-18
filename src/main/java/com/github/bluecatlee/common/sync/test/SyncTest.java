package com.github.bluecatlee.common.sync.test;

import com.github.bluecatlee.common.sync.Mutex;
import com.github.bluecatlee.common.sync.TwinsLock;

import java.util.List;

/**
 * Created by 胶布 on 2020/3/18.
 */
public class SyncTest {

    static class TestMutexService implements Runnable {

        private static final Mutex mutex = new Mutex();

        @Override
        public void run() {
            System.out.println("线程：" + Thread.currentThread().getName() + " 开始执行");
            mutex.lock();
            if (mutex.isLocked()) {
                System.out.println("线程：" + Thread.currentThread().getName() + " 获取锁成功");
            }
            try {
                Thread.sleep(10000);
                if (mutex.hasQueuedThreads()) {
                    System.out.println("线程：" + Thread.currentThread().getName() + " 正在执行，当前有其他线程在等待锁");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                if (mutex.isLocked()) {
                    mutex.unlock();
                    System.out.println("线程：" + Thread.currentThread().getName() + " 释放锁");
                }
            }
        }
    }

    static class TestTwinsLockService implements Runnable {

        private static final TwinsLock mutex = new TwinsLock();

        @Override
        public void run() {
            System.out.println("线程：" + Thread.currentThread().getName() + " 开始执行");
            mutex.lock();
            System.out.println("线程：" + Thread.currentThread().getName() + " 获取锁成功");
            try {
                Thread.sleep(10000);
                if (mutex.hasQueuedThreads()) {
                    System.out.println("线程：" + Thread.currentThread().getName() + " 正在执行，当前有其他线程在等待锁");
                    List<Thread> queuedThreads = mutex.getQueuedThreads();
                    for (Thread thread: queuedThreads) {
                        System.out.println("正在等待获取锁的线程：" + thread.getName());
                    }
                    System.out.println();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                mutex.unlock();
                System.out.println("线程：" + Thread.currentThread().getName() + " 释放锁");
            }
        }
    }

    public static void main(String[] args) {
//        test1();
        test2();
    }

    private static void test1() {
        TestMutexService testService1 = new TestMutexService();
        TestMutexService testService2 = new TestMutexService();

        new Thread(testService1, "TestMutexService1").start();
        new Thread(testService2, "TestMutexService2").start();
    }

    private static void test2() {
        for (int i = 0; i < 10; i++) {
            TestTwinsLockService testTwinsLockService = new TestTwinsLockService();
            new Thread(testTwinsLockService, "testTwinsLockService" + i).start();
        }
    }

}
