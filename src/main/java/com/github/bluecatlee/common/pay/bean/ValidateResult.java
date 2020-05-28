package com.github.bluecatlee.common.pay.bean;

import lombok.Data;

@Data
public class ValidateResult {

	/**
	 * 是否有效
	 */
	private boolean isValid;

	/**
	 * 订单时间
	 */
	private String orderNo;

	/**
	 * 支付金额
	 */
	private String amount;

	/**
	 * 交易流水号
	 */
	private String transNo;

	/**
	 * 交易状态
	 */
	private String status;

	/**
	 * 交易时间
	 */
	private String time;

	/**
	 * 错误码
	 */
	private String errorCode;

	/**
	 * 错误内容
	 */
	private String errorMessage;
	

}
