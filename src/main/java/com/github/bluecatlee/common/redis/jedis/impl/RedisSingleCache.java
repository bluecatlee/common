package com.github.bluecatlee.common.redis.jedis.impl;

import com.github.bluecatlee.common.redis.jedis.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Map;
import java.util.Set;

/**
 * Redis缓存
 */
@Service
@ConditionalOnMissingBean(RedisSentinelCache.class)
public class RedisSingleCache implements RedisCache {

    @Autowired
    private JedisPool jedisPool;

    @Override
    public void init(Map<String, String> config) {
        if (jedisPool == null) {
            // 初始化jedis
            String host = (String) config.get("host");
            String port = (String) config.get("port");
            String database = (String) config.get("database");
            String password = (String) config.get("password");
            if (password != null && password.isEmpty()) {
                password = null;
            }

            JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
            jedisPoolConfig.setMaxTotal(100);
            jedisPoolConfig.setMinIdle(10);
            jedisPoolConfig.setMaxIdle(50);
            jedisPoolConfig.setMaxWaitMillis(50 * 1000);
            jedisPoolConfig.setTestOnBorrow(true);
            jedisPoolConfig.setTestOnReturn(true);

            jedisPool = new JedisPool(jedisPoolConfig, host, Integer.valueOf(port), 3000, password,
                    Integer.valueOf(database));
        }
    }

    @Override
    public void destroy() {
        if (jedisPool != null && !jedisPool.isClosed()) {
            jedisPool.close();
        }
    }

    @Override
    public void put(final String key, final Object object) {
        put(key, object, 0);
    }

    @Override
    public void put(final String key, final Object object, final int exp) {
        if (!(object instanceof String)) {
            throw new RuntimeException("redis value must String");
        }
        Jedis jedis = getJedis();
        if (exp == 0) {
            jedis.set(key, String.valueOf(object));
        } else {
            jedis.setex(key, exp, String.valueOf(object));
        }
        jedis.close();
    }

    @Override
    public Long remove(final String key) {
        Jedis jedis = getJedis();
        Long del = jedis.del(key);
        jedis.close();
        return del;
    }

    @Override
    public Object get(final String key) {
        Jedis jedis = getJedis();
        Object obj = jedis.get(key);
        jedis.close();
        return obj;
    }

    /**
     * 原子减1操作
     * @param key
     * @return
     */
    @Override
    public Long decr(final String key) {
        Jedis jedis = getJedis();
        Long decr = jedis.decr(key);
        jedis.close();
        return decr;
    }

    /**
     * 原子增1操作
     * @param key
     * @return
     */
    @Override
    public Long incr(final String key) {
        Jedis jedis = getJedis();
        Long incr = jedis.incr(key);
        jedis.close();
        return incr;
    }

    /**
     * 链表左push
     * @param key
     * @param values
     * @return
     */
    @Override
    public Long lpush(final String key, final String... values) {
        return lpush(key, 0, values);
    }

    /**
     * 链表左push
     *     备注： 是否需要限制push到链表的元素个数
     * @param key
     * @param values
     * @param exp
     * @return
     */
    @Override
    public Long lpush(final String key, final int exp, final String... values) {
        Jedis jedis = getJedis();
        Long lpush = jedis.lpush(key, values);
        jedis.expire(key, exp);
        jedis.close();
        return lpush;
    }

    /**
     * 链表右pop
     * @param key
     * @return
     */
    @Override
    public String rpop(final String key) {
        Jedis jedis = getJedis();
        String rpop = jedis.rpop(key);
        if (jedis.llen(key).longValue() <= 0) {
            jedis.del(key);
        }
        jedis.close();
        return rpop;
    }

    @Override
    public void sadd(String key, String... str) {
        Jedis jedis = getJedis();
        jedis.sadd(key, str);
        jedis.close();
    }

    @Override
    public Set<String> smembers(String key) {
        Jedis jedis = getJedis();
        Set<String> stringSet = jedis.smembers(key);
        jedis.close();
        return stringSet;
    }

    private Jedis getJedis() {
        if (jedisPool == null) {
            throw new NullPointerException("jedisPool");
        }
        return jedisPool.getResource();
    }

}