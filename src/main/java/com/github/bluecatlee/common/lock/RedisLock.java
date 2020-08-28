package com.github.bluecatlee.common.lock;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;

import java.util.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * 基于redis-setnx实现的一个简单的分布式锁
 *      不可重入、非公平的
 *      没有可靠的续命机制
 *      仅能单机redis使用
 */
public class RedisLock {

    private static final String prefix = "biz:entity_";

    private JedisPool jedisPool;
    private JedisSentinelPool jedisSentinelPool;

    public RedisLock() {
        this.jedisPool = new JedisPool();
    }

    public RedisLock(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public RedisLock(JedisSentinelPool jedisSentinelPool) {
        this.jedisSentinelPool = jedisSentinelPool;
    }


    /**
     * 加锁
     * @param key
     * @param value
     * @param expire
     */
    public void lock(String key, String value, int expire) {
        boolean lock = false;
        do {
            lock = tryLock(key, value, expire);
        } while (!lock);
    }

    /**
     * 加锁
     * @param key
     * @param value
     * @param expire
     * @param timeout 锁等待时间ms
     */
    public boolean tryLock(String key, String value, int expire, int timeout) {
        long waitEnd = System.currentTimeMillis() + timeout;
        while (System.currentTimeMillis() < waitEnd) {
            boolean lock = tryLock(key, value, expire);
            if (lock) {
                return lock;
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        return false;
    }

    /**
     * 加锁
     * @param key
     * @param value
     * @param expire
     * @return
     */
    public boolean tryLock(String key, String value, int expire) {
        String lockKey = prefix + key;
        // String value = UUID.randomUUID().toString();   // value由外部传入

        Jedis jedis = getJedis();

        // Q1
        // 必须要设置超时时间，否则可能导致锁永远没有释放
        // 当使用setnx+expire命令时，如果setnx之后redis宕机将导致锁无法正确释放
        // Long setnx = jedis.setnx(lockKey, uuid);
        // if (setnx == 1) {
        //     jedis.expire(lockKey, expire);
        // }

        // Q2
        // 如果值是过期时间，首先多个客户端的时间不是同步的。其次锁不具备拥有者标识，任何客户端都可以解锁

        try {
            String result = jedis.set(lockKey, value, "NX", "PX", expire);
            if ("OK".equals(result)) {
                return true;
            }
            return false;
        } catch (Exception e) {

        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

        return false;

    }

    /**
     * 解锁
     * @param key
     * @param value
     * @return
     */
    public boolean unlock(String key, String value) {
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

        Jedis jedis = getJedis();
        try {
            String lockKey = prefix + key;
            Object result = jedis.eval(script, Collections.singletonList(lockKey), Collections.singletonList(value));
            if ("1".equals(result)) {
                return true;
            }
            return false;
        } catch (Exception e) {

        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

        return false;

        // 直接通过del命令去删除key是不可行的 因为此时的key可能已经不是当前客户端set的  【谁加的锁由谁释放】
        // if (value.equals(jedis.get(lockKey))) {
        //     jedis.del(key);
        // }

    }

    public void expire(String key, int expire) {
        String lockKey = prefix + key;
        Jedis jedis = getJedis();
        jedis.expire(lockKey, expire);
        jedis.close();
    }

    public String get(String key) {
        String lockKey = prefix + key;
        Jedis jedis = getJedis();
        String result = jedis.get(lockKey);
        jedis.close();
        return result;
    }

    private Jedis getJedis() {
        if (jedisPool != null) {
            return jedisPool.getResource();
        } else if (jedisSentinelPool != null) {
            return jedisSentinelPool.getResource();
        }
        throw new RuntimeException("获取连接失败");
    }

    // ------------------------------------------------- test ---------------------------------------------------------

    public static void main(String[] args) {

        class RedisLockRunner implements Runnable {

            CyclicBarrier cyclicBarrier;

            public RedisLockRunner(CyclicBarrier cyclicBarrier) {
                this.cyclicBarrier = cyclicBarrier;
            }

            @Override
            public void run() {
                try {
                    cyclicBarrier.await();
                    test();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }
        }

        CyclicBarrier cyclicBarrier = new CyclicBarrier(10000);
        for (int i = 0; i < 10000; i++) {
            RedisLockRunner redisLockRunner = new RedisLockRunner(cyclicBarrier);
            new Thread(redisLockRunner).start();
        }

    }

    private static void test() {
        // RedisLock redisLock = new RedisLock();

        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(10000);
        jedisPoolConfig.setMaxIdle(1000);
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, "127.0.0.1", 6379, 1000000);

        // String master = "mymaster";
        // List<String> nodes = new ArrayList<>();
        // nodes.add("192.168.10.89:6800");
        // nodes.add("192.168.10.89:6801");
        // nodes.add("192.168.10.89:6802");
        // Set<String> sentinels = new HashSet<String>(nodes);
        // JedisSentinelPool jedisSentinelPool = new JedisSentinelPool(master, sentinels, jedisPoolConfig);

        // redis分布式锁建议在单台redis上保存 如果使用redis主从配置 由于数据在不同机器上不能及时同步 将导致锁失效
        RedisLock redisLock = new RedisLock(jedisPool);
        // RedisLock redisLock = new RedisLock(jedisSentinelPool);

        String key = "test";
        String uuid = UUID.randomUUID().toString();
        int expire = 1000;
        redisLock.lock(key, uuid, expire);

        // todo 锁续命的问题
        // 每个处理线程对应一个守护线程 在守护线程中进行续命操作 如果使用了线程池，对应的守护线程如何销毁？手动销毁
        // 优雅的销毁一个线程的方式？ 中断机制
        // 如果过期时间设置的很短，即使进行续命，高并发请求一开始进来时锁依旧会失效，因为续命的expire命令排在了前面大量的setnx命令之后
        // 合理设置key的过期时间可能比进行续命要好
        // Thread thread = new Thread(() -> {
        //     while(true) {
        //         if (Thread.currentThread().isInterrupted()) {
        //             // System.out.println(Thread.currentThread().getName() + "退出");
        //             break;
        //         }
        //         String currentValueStr = redisLock.get(key);
        //         if (currentValueStr != null && currentValueStr.equals(uuid)) {
        //             redisLock.expire(key, expire);
        //         }
        //     }
        // });
        // thread.setDaemon(true);
        // thread.setName(Thread.currentThread().getName() + "-Daemon");
        // thread.start();

        // 执行业务逻辑
        try {
            System.out.println(Thread.currentThread().getName() + "开始执行");
            // Thread.sleep(10000);
            Thread.sleep((int)(1000 * Math.random()));
            System.out.println(Thread.currentThread().getName() + "执行结束");
            System.out.println();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (redisLock != null) {
                redisLock.unlock(key, uuid);
            }
            // if (thread != null) {
            //     // thread.stop();
            //     // Debug模式下使用stop方法会导致异常：
            //     // FATAL ERROR in native method: JDWP ExceptionOccurred, jvmtiError=AGENT_ERROR_INVALID_EVENT_TYPE(204)
            //     // JDWP exit error AGENT_ERROR_INVALID_EVENT_TYPE(204): ExceptionOccurred [eventHelper.c:834]
            //
            //     thread.interrupt();
            // }
        }

    }

}
