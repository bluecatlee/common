package com.github.bluecatlee.common.third.shunfeng.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SfProduct {

    /**
     * 商品编号
     */
    private String productID;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 结算单位
     *      0.标准单位
     *      3.斤
     */
    private String measurementType;

    /**
     * 商品重量
     *      结算单位为斤时候使用
     */
    private BigDecimal weight;

    /**
     * 商品单价
     * 1.结算单位为标准单位时，商品总价=商品单价
     * 2.当结算单位为斤时候，商品总价=商品单价*商品重量
     */
    private BigDecimal price;

    /**
     * 商品可售库存
     */
    private Integer inventory;

    /**
     * 图片规格枚举 提供三种尺寸
     * 1.60*60
     * 2.300*300
     * 3.1000*1000
     */
    private String imgType;

    /**
     * 图片地址
     */
    @JsonProperty("imgAddress")
    private String img;

    // /**
    //  * 客户的打折比率
    //  */
    // private BigDecimal custRank;

}

