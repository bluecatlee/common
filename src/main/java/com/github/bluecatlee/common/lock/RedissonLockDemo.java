package com.github.bluecatlee.common.lock;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * 使用redisson分布式锁的demo
 *      redisson使用watch dog提供了锁续命的功能，同时也能在redisson实例异常的时候自动去释放锁
 *      redisson存储的是hash类型的数据 hash的key是标识(guid + threadId)，value是重入次数 以此来实现锁的可重入性
 *      同一个redisson实例且线程id一致时才能保证可重入性
 *
 *      看门狗的超时时间？
 *      非单机模式下锁依旧会失效
 */
public class RedissonLockDemo {

    public static void main(String[] args) {
        class RLockRunner implements Runnable {

            private CyclicBarrier cyclicBarrier;
            private Config config;
            private RedissonClient redissonClient;

            public RLockRunner(Config config, CyclicBarrier cyclicBarrier) {
                this.config = config;
                this.cyclicBarrier = cyclicBarrier;
            }

            public RLockRunner(RedissonClient redissonClient, CyclicBarrier cyclicBarrier) {
                this.redissonClient = redissonClient;
                this.cyclicBarrier = cyclicBarrier;
            }

            @Override
            public void run() {
                try {
                    cyclicBarrier.await();
                    test(config, 0);
                    // test2(redissonClient, 0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }

        }

        Set<String> loggers = new HashSet<>(Arrays.asList("org.redisson", "io.netty"));
        for (String log : loggers) {
            Logger logger = (Logger) LoggerFactory.getLogger(log);
            logger.setLevel(Level.INFO);
            logger.setAdditive(false);
        }

        Config config = new Config();
        String[] addresses = new String[] {"192.168.10.89:6800", "192.168.10.89:6801", "192.168.10.89:6802"};
        config.useSentinelServers().setMasterName("mymaster").setPassword("123456").addSentinelAddress(addresses);

        RedissonClient redissonClient = Redisson.create();
        // RedissonClient redissonClient = Redisson.create(config);

        // CyclicBarrier cyclicBarrier = new CyclicBarrier(10);
        // for (int i = 0; i < 10; i++) {
        //     RLockRunner rLockRunner = new RLockRunner(config, cyclicBarrier);
        //     // RLockRunner rLockRunner = new RLockRunner(redissonClient, cyclicBarrier);
        //     new Thread(rLockRunner).start();
        // }

        // test(config, 0);
        test2(redissonClient, 0);
        System.out.println("finished");

    }

    @SuppressWarnings("all")
    private static void test(Config config, int count) {
        if (count >= 2) {
            return;
        }
        RedissonClient redissonClient = Redisson.create();
        // RedissonClient redissonClient = Redisson.create(config);
        RLock lock = redissonClient.getLock("test9");

        System.out.println("当前线程：" + Thread.currentThread().getName());
        lock.lock();
        try {
            // Thread.sleep(1000);
            System.out.println(Thread.currentThread().getName() + "开始执行");
            Thread.sleep((int)(1000 * Math.random()));
            // 测试是否可重入 不是同一个redissonClient 不能重入
            // test(config, ++count);
            System.out.println(Thread.currentThread().getName() + "执行结束");
            System.out.println();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
            if (redissonClient != null) {
                redissonClient.shutdown();      // 如果不主动shutdown 进程将不会退出 底层有netty线程
            }
        }
    }

    @SuppressWarnings("all")
    private static void test2(RedissonClient redissonClient, int count) {
        if (count >= 2) {
            return;
        }
        RLock lock = redissonClient.getLock("test10");

        System.out.println("当前线程：" + Thread.currentThread().getName());
        lock.lock();
        try {
            // Thread.sleep(1000);
            System.out.println(Thread.currentThread().getName() + "开始执行");
            Thread.sleep((int)(1000 * Math.random()));
            // 测试是否可重入
            test2(redissonClient, ++count);
            System.out.println(Thread.currentThread().getName() + "执行结束");
            System.out.println();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

}
