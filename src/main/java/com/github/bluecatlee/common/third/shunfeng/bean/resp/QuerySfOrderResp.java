package com.github.bluecatlee.common.third.shunfeng.bean.resp;

import com.github.bluecatlee.common.third.shunfeng.bean.SfOrderGoods;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class QuerySfOrderResp {

    /**
     * 订单编号
     */
    private String orderId;

    /**
     * 订单时间 2020-06-28 09:54:11
     */
    private String orderTime;

    /**
     * 收货人名称
     */
    private String receiverName;

    /**
     * 收货人电话
     */
    private String receiverMobile;

    /**
     * 收货人地址
     */
    private String receiverAddress;

    /**
     * 配送时间
     */
    private String deliveryTime;

    /**
     * 订单总价
     */
    private BigDecimal totalPrice;

    /**
     * 订单状态
     */
    private String status;

    /**
     * 出库总金额
     */
    private BigDecimal outStockAmount;

    /**
     * sku列表
     */
    private List<SfOrderGoods> skuList;

}
