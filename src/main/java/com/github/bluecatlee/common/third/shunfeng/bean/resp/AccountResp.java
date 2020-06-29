package com.github.bluecatlee.common.third.shunfeng.bean.resp;

import com.github.bluecatlee.common.third.shunfeng.bean.CustomerAddress;
import com.github.bluecatlee.common.third.shunfeng.bean.WebSite;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class AccountResp {

    /**
     * 商户编号
     */
    private Integer customerNo;

    /**
     * 商户账号
     */
    private String customerID;

    /**
     * 商户名称
     */
    private String customerName;

    /**
     * 站点列表
     */
    private List<WebSite> webSites;

    /**
     * 客户地址列表
     */
    private List<CustomerAddress> customerAddress;

    /**
     * 账户余额
     */
    private BigDecimal totalWaitConfAmt;

    /**
     * 订单总金额
     */
    private BigDecimal totalOrderaAmt;

}
