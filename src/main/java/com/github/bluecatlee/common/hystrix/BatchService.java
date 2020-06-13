package com.github.bluecatlee.common.hystrix;

import java.util.List;

/**
 * 批量服务
 * Created by 胶布 on 2020/6/13.
 */
public interface BatchService<P, R> {

    /**
     * 批处理方法
     * @param params    参数
     * @return          结果
     */
    List<R> batch(List<P> params);

    interface SimpleBatchService extends BatchService<Long, String> {

        String exec(Long id);

        List<String> exec(List<Long> params);

//        default String exec(Long id) {
//            return String.valueOf(id);
//        }
//
//        default  List<String> exec(List<Long> params) {
//            return params.stream().map(String::valueOf).collect(Collectors.toList());
//        }

        default List<String> batch(List<Long> params) {
            return exec(params);
        }
    }

}
