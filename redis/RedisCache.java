import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Map;

/**
 * Redis缓存
 */
@Service("redisCache")
public class RedisCache implements Cache {

    @Autowired
    private JedisPool jedisPool;

    public void init() {
		// 初始化jedis 不注入
    }

    public void destroy() {
        if (jedisPool != null && !jedisPool.isClosed())
            jedisPool.close();
    }

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

    public void remove(final String key) {
        Jedis jedis = getJedis();
        jedis.del(key);
        jedis.close();
    }

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
    public String rpop(final String key) {
        Jedis jedis = getJedis();
        String rpop = jedis.rpop(key);
        if (jedis.llen(key).longValue() <= 0) {
            jedis.del(key);
        }
        jedis.close();
        return rpop;
    }

    private Jedis getJedis() {
        if (jedisPool == null) {
            throw new NullPointerException("jedisPool");
        }
        return jedisPool.getResource();
    }

}