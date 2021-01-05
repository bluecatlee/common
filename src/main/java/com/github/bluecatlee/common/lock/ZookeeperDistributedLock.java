package com.github.bluecatlee.common.lock;

import org.apache.zookeeper.*;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 升级版zookeeper分布式锁
 *      核心lock() unlock()
 *      实际会有锁失效的问题 (已解决 应该是闭锁超时的问题 CountDownLatch超时应该返回false而不是true)
 *      todo 问题： 会话超时(比如会话时间设置过短且gc长时间停顿可能导致zk服务端在超时时间内检测不到客户端心跳)也会导致临时节点被删除，这个时候就会导致锁失效
 *      todo 如果并发数超出了zkServer针对每个ip的最大连接数 则锁会失效。因为此时连接会被服务端主动关闭，且客户端捕获不到异常
 */
public class ZookeeperDistributedLock implements Lock, Watcher {
    private ZooKeeper zk;
    private String root = "/locks";
    private String lockName;
    private String waitNode;
    private String myZnode;
    private CountDownLatch latch;
    private int sessionTimeout = 3000;              // 会话超时时间 注意这个时间不是总的会话时间 而是心跳失败达到这个阈值时连接超时 如果心跳一直保持 则会话一直保持
    private int latchTimeout = 10000;                // 闭锁超时时间
    private List<Exception> exception = new ArrayList();

    public ZookeeperDistributedLock(String config, String lockName) {
        this.lockName = lockName;

        try {
            this.zk = new ZooKeeper(config, this.sessionTimeout, this);             // 绑定当前对象(实现了Watch)作为监听器
//            ZooKeeper.States state = zk.getState();
//            while (ZooKeeper.States.CONNECTING == state) {            // zookeeper连接是异步的 建议连接之后判断状态是否是连接成功 未成功则一直等待 此处可以用闭锁
//                state = zk.getState();                                // 但是  在并发量超过连接限制的情况下 等待连接成功是无效的
//                System.out.println(state);
//                Thread.sleep(1000);
//            }
//            if (ZooKeeper.States.CONNECTED != state) {
//                throw new LockException("连接失败!");
//            }
            Stat stat = this.zk.exists(this.root, false);
            if (stat == null) {
                this.zk.create(this.root, new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (IOException e) {
            this.exception.add(e);
        } catch (KeeperException e) {
            this.exception.add(e);
        } catch (InterruptedException e) {
            this.exception.add(e);
        }

    }

    // 处理监听事件
    public void process(WatchedEvent event) {
        Event.EventType type = event.getType();
        String node = event.getPath();
//        System.out.println("========================" + node + "      " + type);
        if (type != null && type == Event.EventType.NodeDeleted) {      // 优化 仅处理删除节点事件  说明：节点不能存在其他事件 因为事件触发了就会删除
            System.out.println("节点[" + node + "]删除事件触发");
            if (this.latch != null) {
                this.latch.countDown();
                System.out.println(Thread.currentThread().getName() + " release CountDownLatch !");
            }
        }

    }

    // 阻塞式获取锁
    public void lock() {
        if (this.exception.size() > 0) {
            throw new ZookeeperDistributedLock.LockException((Exception)this.exception.get(0));
        } else {
            try {
                if (!this.tryLock()) {
//                    this.waitForLock(this.waitNode, latchTimeout); // 此处最多等待latchTimeout时间后就直接能获取到锁
                    this.waitForLock(this.waitNode, 0);
                    this.lock();
                }
            } catch (KeeperException e) {
                throw new ZookeeperDistributedLock.LockException(e);
            } catch (InterruptedException e) {
                throw new ZookeeperDistributedLock.LockException(e);
            }
        }
    }

    // 非阻塞式获取锁
    public boolean tryLock() {
        try {
            String splitStr = "_lock_";
            if (this.lockName.contains(splitStr)) {
                throw new ZookeeperDistributedLock.LockException("lockName can not contains _lock_");
            } else if (this.myZnode == null) {
                // 创建临时节点 临时节点的前缀为this.lockName + splitStr
                // todo 建议不同业务的分布式锁的节点不使用前缀区分 而是放在不同的目录下
                this.myZnode = this.zk.create(this.root + "/" + this.lockName + splitStr, new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            }
            List<String> subNodes = this.zk.getChildren(this.root, false);
            List<String> lockObjNodes = new ArrayList();
            Iterator iterator = subNodes.iterator();

            while(iterator.hasNext()) {
                String node = (String)iterator.next();
                String _node = node.split(splitStr)[0];
                if (_node.equals(this.lockName)) {
                    lockObjNodes.add(node);                 // 获取前缀一致的所有临时节点集合
                }
            }

            Collections.sort(lockObjNodes);                 // 排序(默认自然排序)
            if (this.myZnode.equals(this.root + "/" + (String)lockObjNodes.get(0))) {   // 如果当前节点是第一个节点(序号最小) 则获得锁
                System.out.println(Thread.currentThread().getName() + " get lock !");
                return true;
            } else {
                String subMyZnode = this.myZnode.substring(this.myZnode.lastIndexOf("/") + 1);
                this.waitNode = (String)lockObjNodes.get(Collections.binarySearch(lockObjNodes, subMyZnode) - 1);   // 设置当前节点的等待节点为其上一个节点
                System.out.println(Thread.currentThread().getName() + " wait lock...  monitor node [" + this.waitNode + "]");
                return false;
            }
        } catch (KeeperException e) {
            throw new ZookeeperDistributedLock.LockException(e);
        } catch (InterruptedException e) {
            throw new ZookeeperDistributedLock.LockException(e);
        }
    }

    // 获取锁 带超时
    public boolean tryLock(long time, TimeUnit unit) {
        try {
            return this.tryLock() ? true : this.waitForLock(this.waitNode, time, unit);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 等待锁
    private boolean waitForLock(String lower, long waitTime, TimeUnit unit) throws InterruptedException, KeeperException {
        Stat stat = this.zk.exists(this.root + "/" + lower, true);
        if (stat != null) {
            this.latch = new CountDownLatch(1);
            if (waitTime == 0) {
                this.latch.await();
                this.latch = null;
                return true;
            } else {
                boolean await = this.latch.await(waitTime, unit);
                this.latch = null;
                return await;
            }
        }

        return true;
    }

    // 等待锁
    private boolean waitForLock(String lower, long waitTime) throws InterruptedException, KeeperException {
        Stat stat = this.zk.exists(this.root + "/" + lower, true);
        if (stat != null) {
            this.latch = new CountDownLatch(1);
            if (waitTime == 0) {
                this.latch.await();
                this.latch = null;
                return true;
            } else {
                boolean await = this.latch.await(waitTime, TimeUnit.MILLISECONDS);
                this.latch = null;
                return await;
            }
        }

        return true;
    }

    // 解锁 todo 解锁失败的报警
    public void unlock() {
        try {
            this.zk.delete(this.myZnode, -1);
            this.zk.close();
            this.myZnode = null;
            System.out.println(Thread.currentThread().getName() + " release lock !");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }

    }

    // 可中断的获取锁 todo 待实现
    public void lockInterruptibly() throws InterruptedException {
        this.lock();
    }

    public Condition newCondition() {
        return null;
    }

    public class LockException extends RuntimeException {

        public LockException(String message) {
            super(message);
        }

        public LockException(Exception e) {
            super(e);
        }
    }

    @Deprecated
    private ZooKeeper.States getStatus() {
        return zk == null ? null : zk.getState();
    }

    public static void main(String[] args) {
//        ZookeeperDistributedLock lock = new ZookeeperDistributedLock("172.17.128.31:2181", "test");
//        lock.lock();
////        try {
////            Thread.sleep(100000000);
////            CountDownLatch latch = new CountDownLatch(1);
////            boolean await = latch.await(10000, TimeUnit.MILLISECONDS);
////            System.out.println(await);
////        } catch (InterruptedException e) {
////            e.printStackTrace();
////        }
//        ZooKeeper.States status = lock.getStatus();
//        System.out.println(status);
//        lock.unlock();

        testConcurrency("172.17.128.31:2181,172.17.128.32:2181,172.17.128.33:2181", "ttt");


    }

    private static boolean testConcurrency(String addresses, String lockName) {

        Set<String> set = new CopyOnWriteArraySet<>();
        Set<String> set2 = new CopyOnWriteArraySet<>();  // 成功获取过锁的线程名
        Set<String> set3 = new CopyOnWriteArraySet<>();  // 获取锁失败的线程名
        AtomicLong counter = new AtomicLong();

        class MyRunnable implements Runnable {
            private CyclicBarrier cyclicBarrier;

            public MyRunnable(CyclicBarrier cyclicBarrier) {
                this.cyclicBarrier = cyclicBarrier;
            }

            @Override
            public void run() {
                try {
                    // 等待所有任务准备就绪
                    cyclicBarrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
                String threadName = Thread.currentThread().getName();
//                    System.out.println(threadName);
                ZookeeperDistributedLock zookeeperLock = new ZookeeperDistributedLock(addresses, lockName);
                try {
                    zookeeperLock.lock();
                } catch (Exception e) {
                    // 获取锁失败
                    set3.add(threadName);
                    return;
                }

                if (counter.get() > 1) {
                    System.out.println("error ================");
                    return;
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                boolean add = set.add(threadName);
                set2.add(threadName);
                if (add) {
                    counter.incrementAndGet();
                }
                zookeeperLock.unlock();
                boolean remove = set.remove(threadName);
                if (remove) {
                    counter.decrementAndGet();
                }

            }
        }

        long startTime = System.currentTimeMillis();

        int count = 100;
        CyclicBarrier cyclicBarrier = new CyclicBarrier(count);
        ExecutorService executorService = Executors.newFixedThreadPool(count);
        for (int i = 0; i < count; i++) {
            executorService.execute(new MyRunnable(cyclicBarrier));
        }
        executorService.shutdown();
        while (!executorService.isTerminated()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Execute time：" + ((endTime - startTime) / 1000) + " s");

        System.out.println(set2.size());
        System.out.println(set3.size());
//        System.out.println(counter.get());
//        System.out.println("-----------------");
        if (set2.size() != count) {
            System.out.println("FAILURE!!! There are duplicate thread name");
            System.out.println("Count of unrepetable thread names: " + set.size());
            return false;
        } else {
            System.out.println("SUCCESS!!!");
            return true;
        }


    }

}

