package com.github.bluecatlee.common.third.shunfeng.bean;

import lombok.Data;

import java.util.List;

@Data
public class SfOrder {

    /**
     * 小站编号
     */
    private Integer deskSiteNo;

    /**
     * 配送方式 13物流配送 27自提
     */
    private Integer deliveryType;

    /**
     * 客户地址编号
     */
    private Integer customerAddressNo;

    /**
     * 配送时间 1早晨 2中午 3下午 4晚上
     */
    private Integer deliveryTime;

    /**
     * 商品SKU列表
     */
    private List<SfOrderGoods> skuList;

}
