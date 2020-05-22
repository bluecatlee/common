package com.github.bluecatlee.common.redis.jedis.impl;

import com.github.bluecatlee.common.redis.jedis.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;

import java.util.Map;

/**
 * Redis缓存
 */
@Service
// @Order(1)
@Profile("redissentinel")
public class RedisSentinelCache extends RedisCache {

    @Autowired
    private JedisSentinelPool jedisSentinelPool;

    @Override
    public void init(Map<String, String> config) {
    }

    @Override
    public void destroy() {
        if (jedisSentinelPool != null && !jedisSentinelPool.isClosed()) {
            jedisSentinelPool.close();
        }
    }

    @Override
    public Jedis getJedis() {
        if (jedisSentinelPool == null) {
            throw new NullPointerException("jedisPool");
        }
        return jedisSentinelPool.getResource();
    }

}