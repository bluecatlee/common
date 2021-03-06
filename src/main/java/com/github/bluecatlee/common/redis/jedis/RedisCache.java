package com.github.bluecatlee.common.redis.jedis;

import com.github.bluecatlee.common.cache.Cache;
import redis.clients.jedis.Jedis;

import java.util.Map;
import java.util.Set;

/**
 * Redis缓存
 */
public abstract class RedisCache implements Cache {

    protected abstract void init(Map<String, String> config);

    protected abstract void destroy();

    public void put(final String key, final Object object) {
        put(key, object, 0);
    }

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

    public Long remove(final String key) {
        Jedis jedis = getJedis();
        Long del = jedis.del(key);
        jedis.close();
        return del;
    }

    public Object get(final String key) {
        Jedis jedis = getJedis();
        Object obj = jedis.get(key);
        jedis.close();
        return obj;
    }

    public Long decr(final String key) {
        Jedis jedis = getJedis();
        Long decr = jedis.decr(key);
        jedis.close();
        return decr;
    }

    public Long incr(final String key) {
        Jedis jedis = getJedis();
        Long incr = jedis.incr(key);
        jedis.close();
        return incr;
    }

    public Long lpush(final String key, final String... values) {
        return lpush(key, 0, values);
    }

    public Long lpush(final String key, final int exp, final String... values) {
        Jedis jedis = getJedis();
        Long lpush = jedis.lpush(key, values);
        jedis.expire(key, exp);
        jedis.close();
        return lpush;
    }

    public String rpop(final String key) {
        Jedis jedis = getJedis();
        String rpop = jedis.rpop(key);
        if (jedis.llen(key).longValue() <= 0) {
            jedis.del(key);
        }
        jedis.close();
        return rpop;
    }

    public void sadd(String key, String... str) {
        Jedis jedis = getJedis();
        jedis.sadd(key, str);
        jedis.close();
    }

    public Set<String> smembers(String key) {
        Jedis jedis = getJedis();
        Set<String> stringSet = jedis.smembers(key);
        jedis.close();
        return stringSet;
    }

    protected abstract Jedis getJedis();

}