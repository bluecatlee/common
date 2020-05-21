package com.github.bluecatlee.common;

import com.github.bluecatlee.common.redis.RedisCache;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CommonApplication.class)
@ActiveProfiles("redissentinel")
public class RedisSentinelTest {

    @Autowired
    private RedisCache redisCache;

    @Test
    public void testRedisSentinel() {
        String key = "testkey1";
        String val = "heihei";
        redisCache.put(key, val);
        String value = (String)redisCache.get(key);
        Assert.assertEquals(value, val);
    }

}
