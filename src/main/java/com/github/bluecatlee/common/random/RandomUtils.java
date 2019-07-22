package com.github.bluecatlee.common.random;

import java.math.BigDecimal;
import java.util.Random;

public class RandomUtils {

    public static final String ALLCHAR = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static String generateString(int length) {
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(ALLCHAR.charAt(random.nextInt(ALLCHAR.length())));
        }
        return sb.toString().toUpperCase();
    }

    // public static BigDecimal randomBigDecimal1(BigDecimal min, BigDecimal max) {
    //     float maxf = max.floatValue(), minf = min.floatValue();
    //     BigDecimal db = new BigDecimal(Math.random() * (maxf - minf) + minf);
    //     db.setScale(2, BigDecimal.ROUND_HALF_EVEN);   // 保留2位小数并四舍五入
    //     return db;
    // }

    /**
     * 随机获取两个BigDecimal数之间的某个值
     * @param min 最小值
     * @param max 最大值
     * @return
     */
    public static BigDecimal randomBigDecimal(BigDecimal min, BigDecimal max) {
        if (min.compareTo(max) > 0) {
            throw new RuntimeException("min value cannot be greater than max value");
        }
        if (min.compareTo(max) == 0) {
            return min;
        }
        int scale = max.scale() > min.scale() ? max.scale() : min.scale(); // 位数取大的

        int maxV = max.multiply(new BigDecimal(Math.pow(10, scale))).intValue();
        int minV = min.multiply(new BigDecimal(Math.pow(10, scale))).intValue();
        int randomV = ((int) Math.rint(Math.random() * (maxV - minV) + minV)); // 转成整数后取随机数

        BigDecimal bigDecimal = new BigDecimal(String.valueOf(randomV / Math.pow(10, scale))).setScale(scale);

        // if (bigDecimal.compareTo(max) > 0 || bigDecimal.compareTo(min) < 0) {
        //     throw new RuntimeException("error");
        // }

        return bigDecimal;
    }

    public static void main(String[] args) {
        BigDecimal min = new BigDecimal("10.01");
        BigDecimal max = new BigDecimal("50.99");
        for (int i = 0; i < 1000000; i++) {
            // System.out.println(randomBigDecimal1(min, max));
            System.out.println(randomBigDecimal(min, max));
        }
    }

}
