package com.github.bluecatlee.common.third.shunfeng.bean;

import lombok.Data;

/**
 * 公共请求参数
 */
@Data
public class SfCommonReqParams {

    /**
     * 分配给渠道的AppKey
     */
    private String appKey;

    /**
     * 渠道请求参数的签名串
     */
    private String sign;

    /**
     * 请求时间戳 采用yyyyMMddHHmmss的形式
     */
    private String timestamp;

    /**
     * 商户号
     */
    private String merchantId;

}
