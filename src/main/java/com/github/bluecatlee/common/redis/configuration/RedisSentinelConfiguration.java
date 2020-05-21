package com.github.bluecatlee.common.redis.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
@ConfigurationProperties(prefix = "spring.redis.sentinel")
@Data
public class RedisSentinelConfiguration {

    /**
     * 一般都是把属性封装成property类 更合理一点
     */
    @Autowired
    private CacheConfiguration cacheConfiguration;

    private String master;

    private List<String> nodes;

    @Bean
    public JedisSentinelPool jedisSentinalPool() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(100);// 设置最大连接数
        config.setMaxWaitMillis(60000);// 设置最大阻塞时间，记住是毫秒数milliseconds
        config.setMaxIdle(10);// 设置空间连接
        Set<String> sentinels = new HashSet<String>(nodes);
        JedisSentinelPool pool  = new JedisSentinelPool(master, sentinels, config, cacheConfiguration.getPassword());
        return pool;
    }

}
