import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    @Autowired
    private StringRedisTemplate template;

    public static final long EXPIRE_MINUTES = 120L;  //默认过期时间

    /**
     * 设置键值
     * @param key
     * @param value
     */
    public void set(String key, String value) {
        template.opsForValue().set(key, value, EXPIRE_MINUTES, TimeUnit.MINUTES);
    }

    /**
     * 设置键的过期时间
     * @param key
     * @param minutes
     */
    public void expire(String key, long minutes) {
        template.expire(key, minutes, TimeUnit.MINUTES);
    }

    /**
     * 获取键值集合
     * @param pattern 正则
     * @return
     */
    public Set<String> keys(String pattern) {
        return template.keys(pattern);
    }

    /**
     * 获取值
     * @param key
     * @return
     */
    public String get(String key) {
        return template.opsForValue().get(key);
    }

    /**
     * 删除
     * @param key
     * @return
     */
    public boolean delete(String key) {
        return template.delete(key);
    }

    /**
     * 批量删除
     * @param keys
     */
    public void delete(Collection<String> keys) {
        template.delete(keys);
    }

    /**
     * 对存储在指定key的数值执行原子的加1操作
     * @param key
     * @return
     */
    public Long incrKey(String key){
        return template.execute((RedisCallback<Long>) connection ->
                connection.incr(template.getStringSerializer().serialize(key))
        );
    }


}