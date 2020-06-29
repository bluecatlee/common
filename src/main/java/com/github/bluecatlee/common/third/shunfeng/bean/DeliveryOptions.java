package com.github.bluecatlee.common.third.shunfeng.bean;

import lombok.Data;

/**
 * 配送时间选项
 */
@Data
public class DeliveryOptions {

    /**
     * 配送时间
     *      1：早晨
     *      2：中午
     *      3：下午
     *      4：晚上
     */
    private Integer deliTime;

    /**
     * 配送时间描述 非必回
     */
    private String deliDesc;

    /**
     * 配送延后天数 数值型，若为N，则N天后配送
     */
    private Integer deliPostponeDays;

}
