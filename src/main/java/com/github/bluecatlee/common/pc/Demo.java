package com.github.bluecatlee.common.pc;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 生产者消费者模型的一个demo
 */
public class Demo {

    public static void main(String[] args) {

        Queue queue = new Queue();
        new Thread(new Producer(queue)).start();
        new Thread(new Producer(queue)).start();
        new Thread(new Consumer(queue)).start();

    }

    static class Producer implements Runnable {

        Queue queue;

        Producer(Queue queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            try {
                for (int i = 0; i < 100; i++) {
                    queue.put(i);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    static class Consumer implements Runnable {

        Queue queue;

        Consumer(Queue queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            try {
                for (int i = 0; i < 100; i++) {
                    Object value = queue.take();
                    System.out.println(value);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    static class Queue {
        Lock lock = new ReentrantLock();
        Condition pro = lock.newCondition();
        Condition con = lock.newCondition();

        final int CAPACITY = 10;
        Object[] container = new Object[CAPACITY];      // 用数组实现一个有界队列
        int count = 0;
        int putIndex = 0;
        int takeIndex = 0;

        public void put(Object element) throws InterruptedException{
            lock.lock();
            try {
                while (count >= CAPACITY) {
                    pro.await();                // 如果队列已满 则阻塞生产者
                }
                container[putIndex] = element;
                putIndex++;
                if (putIndex >= CAPACITY) {
                    putIndex = 0;
                }
                count++;
                con.signalAll();                // 一旦生产了新元素 则立刻通知所有消费者
            } finally {
                lock.unlock();
            }
        }

        public Object take() throws InterruptedException{
            lock.lock();
            try {
                while (count <= 0) {
                    con.await();                // 如果队列为空 则阻塞消费者
                }
                Object element = container[takeIndex];
                takeIndex++;
                if (takeIndex >= CAPACITY) {
                    takeIndex = 0;
                }
                count--;
                pro.signalAll();                // 一旦有元素消费 则立即通知所有生产者
                return element;
            } finally {
                lock.unlock();
            }
        }

    }

}
