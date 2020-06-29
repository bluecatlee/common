package com.github.bluecatlee.common.third.shunfeng.bean;

import lombok.Data;

/**
 * 客户地址
 */
@Data
public class CustomerAddress {

    /**
     * 地址编号
     */
    private Integer addrNo;

    /**
     * 收货手机
     */
    private String cellPhone;

    /**
     * 收货省名称
     */
    private String provName;

    /**
     * 收货省编号
     */
    private Integer provSysNo;

    /**
     * 收货市名称 非必回
     */
    private String cityName;

    /**
     * 收货市编号 非必回
     */
    private Integer citySysNo;

    /**
     * 收货区名称 非必回
     */
    private String areaName;

    /**
     * 收货区编号 非必回
     */
    private Integer areaSysNos;

    /**
     * 地址详情
     */
    private String addressDetail;


}
