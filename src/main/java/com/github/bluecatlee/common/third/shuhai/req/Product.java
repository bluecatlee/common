package com.github.bluecatlee.common.third.shuhai.req;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class Product {
	
	/**
	 * 客户商品编号 必填
	 */
	private String productId;
	
	/**
	 * 下单数量（小数2位精度）必填  
	 */
	private BigDecimal quantity;
	
	/**
	 * 实发数量(小数精度，2位)
	 */
	private BigDecimal sendQuantity;
	
	/**
	 * 实际签收数量(小数精度，2位)
	 */
	private BigDecimal signQuantity;
	
	/**
	 * 客户同步实收数量(小数精度，2位)
	 */
	private BigDecimal actualQuantity;
	
	/**
	 * 规格,比如：10422424:5kg/包，对于SPU为非必填项、对于SKU为必填项
	 */
	private String spec;
	
	/**
	 * 订单数量 （小数2位精度）【发货业务返回字段】
	 */
	private BigDecimal purchaseNumber;
	
	/**
	 * 实发数量 （小数2位精度）【发货业务返回字段】
	 */
	private BigDecimal actualSendNumber;
	
}
