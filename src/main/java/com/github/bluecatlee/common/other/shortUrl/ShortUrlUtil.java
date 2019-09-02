package com.github.bluecatlee.common.other.shortUrl;

import redis.clients.jedis.Jedis;

public class ShortUrlUtil {

    private static final String SHORT_URL_KEY = "SHORT_URL_KEY";

    private static final String LOCALHOST = "http://localhost:4444/";

    private static final String SHORT_LONG_PREFIX = "short_long_prefix_";

    private static final String CACHE_KEY_PREFIX = "cache_key_prefix_";

    private static final int CACHE_SECONDS = 1 * 60 * 60;

    private final String redisConfig;

    private final Jedis jedis;

    public ShortUrlUtil(String redisConfig) {
        this.redisConfig = redisConfig;
        this.jedis = new Jedis(this.redisConfig);
    }

    public String getShortUrl(String longUrl, int seed) {
        // 查询缓存
        // String cache = jedis.get(CACHE_KEY_PREFIX + longUrl);
        // if (cache != null) {
        //     return LOCALHOST + toOtherBaseString(Long.valueOf(cache), decimal.x);
        // }

        // 自增 (可以通过snowflake算法生成的发号器实现短url)
        long num = jedis.incr(SHORT_URL_KEY);
        // 在数据库中保存短-长URL的映射关系,可以保存在MySQL中
        jedis.set(SHORT_LONG_PREFIX + num, longUrl);

        // 写入缓存
        // jedis.setex(CACHE_KEY_PREFIX + longUrl, CACHE_SECONDS, String.valueOf(num));
        return LOCALHOST + toOtherBaseString(num, seed);

    }

    /**
     * 在进制表示中的字符集合
     */
    final static char[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
                                    'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
                                    'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

    /**
     * 由10进制的数字转换到其他进制 最大支持62进制
     */
    private String toOtherBaseString(long n, int base) {
        long num = 0;
        if (n < 0) {
            num = ((long) 2 * 0x7fffffff) + n + 2;
        } else {
            num = n;
        }
        char[] buf = new char[32];
        int charPos = 32;

        while ((num / base) > 0) {
            buf[--charPos] = digits[(int) (num % base)];
            num /= base;
        }
        buf[--charPos] = digits[(int) (num % base)];
        return new String(buf, charPos, (32 - charPos));

    }

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            System.out.println(new ShortUrlUtil("localhost").getShortUrl("www.baidudu.com/" + i, 62));
        }
    }
}