package com.github.bluecatlee.common.third.shuhai.req;

import java.util.List;

import lombok.Data;

@Data
public class SHRefund {

	
	private String orderId;
	
	private String shopCode;
	
	/**
	 * 蜀海销售退货单
	 */
	private String refundOrderId;
	
	/**
	 * 退货日期 格式为yyyy-MM-dd
	 */
	private String refundDate;
	
	/**
	 * 退货类型 Z01质量问题 Z08其它  Z09客户原因 Z10包装问题    Z11  保质期问题   Z12数量问题（送货差异）

	 */
	private String refundType;
	
	private List<Product> items;
	
}
