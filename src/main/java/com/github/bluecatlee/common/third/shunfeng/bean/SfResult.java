package com.github.bluecatlee.common.third.shunfeng.bean;

import lombok.Data;

/**
 * 顺丰优选响应结果
 * @param <T>
 */
@Data
public class SfResult<T> {

    /**
     * 必回。状态码，200为成功，其余为失败
     */
    private String status;

    /**
     * 错误说明，当status!= 200时返回
     */
    private String errorMsg;

    /**
     * 业务数据，当status== 200时返回
     */
    private T data;

}
