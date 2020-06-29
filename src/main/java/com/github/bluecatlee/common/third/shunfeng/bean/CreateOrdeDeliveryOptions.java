package com.github.bluecatlee.common.third.shunfeng.bean;

import lombok.Data;

import java.util.List;

/**
 * 创建订单及配送时间选择
 */
@Data
public class CreateOrdeDeliveryOptions {

    /**
     * 下单开始时间HH:mm:ss
     */
    private String creaOrdeStartTime;

    /**
     * 下单结束时间 HH:mm:ss
     */
    private String creaOrdeEndTime;

    /**
     * 配送时间选择
     */
    private List<DeliveryOptions> deliOptis;

}
