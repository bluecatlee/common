package com.github.bluecatlee.common.lock;

import org.I0Itec.zkclient.IZkConnection;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.apache.zookeeper.ZooKeeper;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 基于zookeeper的分布式锁
 *      核心是利用其临时有序节点的特性：如果当前节点是顺序最小的，则获取锁；连接关闭后释放锁。
 *      待优化： 只有一把锁 只能同一个业务使用。需要根据业务将临时节点放在不同的目录下
 *      todo 问题： 会话超时(比如会话时间设置过短且gc长时间停顿可能导致zk服务端在超时时间内检测不到客户端心跳)也会导致临时节点被删除，这个时候就会导致锁失效
 *      todo 如果并发数超出了zkServer针对每个ip的最大连接数 则锁会失效。因为此时连接会被服务端主动关闭，且客户端捕获不到异常
 */
public class ZookeeperLock {

    private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ZookeeperLock.class);
    private String beforePath;
    private String currentPath;
    private CountDownLatch latch = null;
    private static String keeperAddress = "10.203.1.44:2181";       // zkServer的地址，多个地址逗号分隔
    protected static String path = "/locks";                     // 分布式锁使用的根节点 持久节点 下面的子节点是临时节点
    protected ZkClient client;

    public ZookeeperLock() {
        this(keeperAddress);
    }

    public ZookeeperLock(String addresses) {
        this.client = new ZkClient(addresses);
        if (!this.client.exists(path)) {
            this.client.createPersistent(path, "lock");
        }
    }

    /**
     * 获取锁 阻塞式
     */
    public void getLock() {
        if (this.tryLock()) {   // 尝试获取锁
            logger.info(Thread.currentThread().getName() + " get lock !");
        } else {
            this.waitForLock(); // 阻塞等待锁
            this.getLock();     // 递归调用
        }
    }

    /**
     * 释放锁
     */
    public void unLock() {
        if (this.client != null) {
            this.client.delete(this.currentPath);   // 主动删除节点而不是等待连接释放后自动删除节点
            this.client.close();
        }
    }

    // 等待锁
    public void waitForLock() {
        IZkDataListener listener = new IZkDataListener() {
            public void handleDataDeleted(String dataPath) throws Exception {
                if (ZookeeperLock.this.latch != null) {
                    ZookeeperLock.this.latch.countDown();     // 节点删除事件触发后递减计数器
                }
            }
            public void handleDataChange(String arg0, Object arg1) throws Exception {
            }
        };
        // 注册监听节点事件 监听的是当前节点的前一个节点
        this.client.subscribeDataChanges(this.beforePath, listener);
        if (this.client.exists(this.beforePath)) {      // 如果前一个节点已经不存在，这个时候应该跳过，重新尝试获取锁
            this.latch = new CountDownLatch(1);         // 创建闭锁
            try {
                this.latch.await();                     // 等待计数器到0
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // 解除监听节点事件
        this.client.unsubscribeDataChanges(this.beforePath, listener);
    }

    // 非阻塞方式获取锁
    public boolean tryLock() {
        if (null == this.currentPath || this.currentPath.length() <= 0) {
            this.currentPath = this.client.createEphemeralSequential(path + "/", "lock");   // 创建临时节点，返回创建的子节点
        }
        java.util.List<String> childrens = this.client.getChildren(path);
        Collections.sort(childrens);                                             // 获取子节点并排序(默认自然排序)
        if (this.currentPath.equals(path + "/" + (String)childrens.get(0))) {    // 如果当前节点是序号最小的 表示获取锁成功
            return true;
        } else {
            int wz = Collections.binarySearch(childrens, this.currentPath.substring(path.length() + 1));  // 查找当前节点的位置
            this.beforePath = path + "/" + (String)childrens.get(wz - 1);       // 设置当前节点的前一个节点
            return false;
        }
    }

    @Deprecated
    private ZooKeeper.States getStatus() {
        Class<? extends ZkClient> aClass = client.getClass();
        Field[] declaredFields = aClass.getDeclaredFields();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            if (field.getName().equals("_connection")) {
                try {
                    ZkConnection o = (ZkConnection)field.get(client);
                    if (o != null) {
                        Class<? extends IZkConnection> aClass1 = o.getClass();
                        Field[] declaredFields2 = aClass1.getDeclaredFields();
                        for (Field field2 : declaredFields2) {
                            field2.setAccessible(true);
                            if (field2.getName().equals("_zk")) {
                                ZooKeeper zooKeeper = (ZooKeeper)field2.get(o);
                                ZooKeeper.States state = zooKeeper.getState();
                                return state;
                            }
                        }
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static void main(String[] args) {

//        ZookeeperLock zookeeperDistributeLock = new ZookeeperLock();
//        System.out.println(zookeeperDistributeLock.getStatus());
//        zookeeperDistributeLock.getLock();
//        zookeeperDistributeLock.unLock();
//        System.out.println(zookeeperDistributeLock.getStatus());

//        String addresses = "10.203.1.43:2181,10.203.1.44:2181,10.203.1.45:2181";
        String addresses = "172.17.128.31:2181";
        testConcurrency(addresses);
    }

    private static boolean testConcurrency(String addresses) {

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
                ZookeeperLock zookeeperLock = new ZookeeperLock(addresses);
                try {
                    zookeeperLock.getLock();
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
                    // 每次获得锁的时候 必须没有其他线程获得锁
                    boolean add = set.add(threadName);
                    set2.add(threadName);
                    if (add) {
                        counter.incrementAndGet();
                    }
                    zookeeperLock.unLock();
                    boolean remove = set.remove(threadName);
                    if (remove) {
                        counter.decrementAndGet();
                    }

            }
        }

        long startTime = System.currentTimeMillis();

        int count = 300;
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

