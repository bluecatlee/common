package com.github.bluecatlee.common.third.shunfeng.bean.resp;

import com.github.bluecatlee.common.third.shunfeng.bean.SfOrderGoods;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateSfOrderResp {

    /**
     * 订单编号
     */
    private String orderId;

    /**
     * 地址编号
     */
    private Integer customerAddressNo;

    /**
     * 订单总金额
     */
    private BigDecimal totalPrice;

    /**
     * 小站编号
     */
    private Integer deskSiteNo;

    /**
     * 大站编号
     */
    private Integer webSiteNo;

    /**
     * 订单商品列表
     */
    private List<SfOrderGoods> skuList;

}
