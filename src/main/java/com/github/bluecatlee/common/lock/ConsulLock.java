package com.github.bluecatlee.common.lock;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.kv.model.PutParams;
import com.ecwid.consul.v1.session.model.NewSession;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * 基于Consul的K/V存储功能实现的一个简易的锁demo
 */
public class ConsulLock {

    // 同步锁参数前缀
    private static final String prefix = "lock/";

    private String sessionId;
    private String sessionName;
    private String lockKey;

    private ConsulClient consulClient;

    /**
     *
     * @param consulClient
     * @param sessionName   同步锁的session名称
     * @param lockKey       同步锁在consul的KV存储中的Key路径，会自动增加prefix前缀，方便归类查询
     */
    public ConsulLock(ConsulClient consulClient, String sessionName, String lockKey) {
        this.consulClient = consulClient;
        this.sessionName = sessionName;
        this.lockKey = prefix + lockKey;
    }

    /**
     * 获取锁
     * @param block
     * @return
     */
    public Boolean lock(boolean block) {
        if (sessionId != null) {
            throw new RuntimeException(sessionId + " - Already locked!");
        }
        sessionId = createSession(sessionName);
        while(true) {
            PutParams putParams = new PutParams();
            putParams.setAcquireSession(sessionId);
            if(consulClient.setKVValue(lockKey, "lock:" + LocalDateTime.now(), putParams).getValue()) {
                return true;
            } else if(block) {
                continue;
            } else {
                return false;
            }
        }
    }

    /**
     * 释放同步锁
     *
     * @return
     */
    public Boolean unlock() {
        PutParams putParams = new PutParams();
        putParams.setReleaseSession(sessionId);
        boolean result = consulClient.setKVValue(lockKey, "unlock:" + LocalDateTime.now(), putParams).getValue();
        consulClient.sessionDestroy(sessionId, null);
        return result;
    }

    /**
     * 创建session
     * @param sessionName
     * @return
     */
    private String createSession(String sessionName) {
        NewSession newSession = new NewSession();
        newSession.setName(sessionName);
        return consulClient.sessionCreate(newSession, null).getValue();
    }

    public static void main(String[] args) {

        class LockRunner implements Runnable {

            private int flag;

            public LockRunner(int flag) {
                this.flag = flag;

            }

            @Override
            public void run() {
                ConsulLock lock = new ConsulLock(new ConsulClient(), "lock-session", "lock-key");
                try {
                    if (lock.lock(true)) {
                        System.out.println("Thread " + flag + " start!");
                        Thread.sleep(new Random().nextInt(3000));
                        System.out.println("Thread " + flag + " end!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }
        }

        // ConsulClient包装了HttpClient HttpClient请求时会打印出大量DEBUG日志 此处屏蔽
        Set<String> loggers = new HashSet<>(Arrays.asList("org.apache.http"));
        for (String log : loggers) {
            Logger logger = (Logger) LoggerFactory.getLogger(log);
            logger.setLevel(Level.INFO);
            logger.setAdditive(false);
        }

        new Thread(new LockRunner(1)).start();
        new Thread(new LockRunner(2)).start();
        new Thread(new LockRunner(3)).start();
        new Thread(new LockRunner(4)).start();
        new Thread(new LockRunner(5)).start();

        try {
            Thread.sleep(20000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


}
