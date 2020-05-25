package com.github.bluecatlee.common.redis.jedis.impl;

import com.github.bluecatlee.common.redis.jedis.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Map;

/**
 * Redis缓存
 */
@Service
// @ConditionalOnMissingBean(RedisSentinelCache.class)
@Profile("!redissentinel")
public class RedisSingleCache extends RedisCache {

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
    public Jedis getJedis() {
        if (jedisPool == null) {
            throw new NullPointerException("jedisPool");
        }
        return jedisPool.getResource();
    }

}