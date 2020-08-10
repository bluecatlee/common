package com.github.bluecatlee.common.filter;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

/**
 * 布隆过滤器demo 直接使用google guava组件提供的BloomFilter实现
 *      主要用于处理缓存穿透
 *      布隆过滤器只能判断数据一定不存在，不能判断数据一定存在
 *
 *      例如商品详情页静态化后存在缓存之中。考虑在项目启动时 读取数据库中所有的货品id集合存储到布隆过滤器中
 *      在请求数据时，首先通过布隆过滤器判断数据是否不存在，如果不存在直接返回单独的商品不存在页面，这样就没有必要去请求缓存，请求数据库，能够极大的降低缓存穿透出现的概率；
 *      在有新商品上架时可以同步将数据新增到布隆过滤器中。
 */
public class MyBloomFilter {

    //预计要插入多少数据
    private static int size = 1000000;

    //期望的误判率
    private static double fpp = 0.01;

    private static BloomFilter<Integer> bloomFilter = BloomFilter.create(Funnels.integerFunnel(), size, fpp);

    public static void main(String[] args) {
        //插入数据
        for (int i = 0; i < 1000000; i++) {
            bloomFilter.put(i);
        }
        int count = 0;
        for (int i = 1000000; i < 2000000; i++) {
            if (bloomFilter.mightContain(i)) {
                count++;
                System.out.println(i + "误判了");
            }
        }
        System.out.println("总共的误判数:" + count);
    }

}
