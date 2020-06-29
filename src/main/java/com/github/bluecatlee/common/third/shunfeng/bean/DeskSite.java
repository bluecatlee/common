package com.github.bluecatlee.common.third.shunfeng.bean;

import lombok.Data;

import java.util.List;

@Data
public class DeskSite {

    /**
     * 小站编号
     */
    private Integer siteNo;

    /**
     * 小站名称
     */
    private String siteName;

    /**
     * 小站覆盖范围
     */
    private List<CoverScope> coverScopes;

    /**
     * 创建订单及配送时间选择
     */
    private List<CreateOrdeDeliveryOptions> creaOrdeDeliveryOptions;
}
