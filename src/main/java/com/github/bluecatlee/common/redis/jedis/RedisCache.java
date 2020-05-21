package com.github.bluecatlee.common.redis.jedis;

import com.github.bluecatlee.common.cache.Cache;

import java.util.Map;
import java.util.Set;

/**
 * Redis缓存
 */
public interface RedisCache extends Cache {

    void init(Map<String, String> config);

    void destroy();

    void put(final String key, final Object object);

    void put(final String key, final Object object, final int exp);

    Long remove(final String key);

    Object get(final String key);

    Long decr(final String key);

    Long incr(final String key);

    Long lpush(final String key, final String... values);

    Long lpush(final String key, final int exp, final String... values);

    String rpop(final String key);

    void sadd(String key, String... str);

    Set<String> smembers(String key);

}