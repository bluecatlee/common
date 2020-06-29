package com.github.bluecatlee.common.third.shunfeng.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SfOrderGoods {

    /**
     * 商品编号
     */
    private String skuId;

    /**
     * 商品数量
     */
    private Integer count;

    /**
     * 商品价格
     */
    private BigDecimal price;

    /**
     * 商品重量
     */
    private BigDecimal weight;

    /**
     * 金额小计 该商品结算金额小计
     */
    private BigDecimal amount;


    /*以下字段仅在获取订单详情时返回*/
    /**
     * 商品名称
     */
    private String name;

    /**
     * 结算单位 0：标准单位 3：斤
     */
    private Integer measurementType;

    /**
     * 订购数量
     */
    private BigDecimal boxCount;

    /**
     * 出库数量
     */
    private BigDecimal outStockCount;

    /**
     * 退货数量
     */
    @JsonProperty("ReturnedCount")
    private BigDecimal returnedCount;

}
