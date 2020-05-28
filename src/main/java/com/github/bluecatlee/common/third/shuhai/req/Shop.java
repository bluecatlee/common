package com.github.bluecatlee.common.third.shuhai.req;

import java.util.List;

import lombok.Data;

@Data
public class Shop {
	
	/**
	 * 客户门店编码 必填
	 */
	private String shopCode;
	
	/**
	 * 要求交货日期，格式为yyyy-MM-dd  后续业务中表示实际交货日期
	 */
	private String deliveryDate;

	/**
	 * 商品集合
	 */
	private List<Product> products;
	
	
	
	// 以下字段在退货业务中会有返回
	
	/**
	 * 蜀海销售退货单编号
	 */
	private String refundOrderId;
	
	/**
	 * 退货日期
	 */
	private String refundDate;
	
	/**
	 * 退货类型   Z01质量问题  Z08其它   Z09客户原因  Z10包装问题    Z11  保质期问题   Z12数量问题（送货差异）
	 */
	private String refundType;
	
}
