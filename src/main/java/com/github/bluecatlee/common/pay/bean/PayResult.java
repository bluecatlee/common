package com.github.bluecatlee.common.pay.bean;

import lombok.Data;

@Data
public class PayResult {

	/**
	 * 盛付通支付链接地址
	 */
	private String url;
	
	/**
	 * 微信支付返回结果
	 */
	private String wechatPayResult;

	/**
	 * ali支付结果
	 */
	private String aliPayResult;
	
}
