package com.github.bluecatlee.common.pay.bean;

import java.util.Map;

import lombok.Data;

@Data
public class Refund {

	/**
	 * 商户号
	 */
	private String partner;
	
	/**
	 * 密钥
	 */
	private String partnerKey;
	
	/**
	 * 后台回调地址
	 */
	private String notifyURL;
	
	/**
	 * 类型
	 */
	private String type;

	/**
	 * 原始订单号
	 */
	private String originalOrderNo;
	
	/**
	 * 退款订单号
	 */
	private String refundOrderNo;
	
	/**
	 * 退款金额
	 */
	private String refundAmount;
	
	/**
	 * 扩展字段
	 */
	private Map<String, String> ext;
	
}
