/**
 * 
 */
package com.github.bluecatlee.common.pay.service;

import com.github.bluecatlee.common.pay.bean.*;

import java.util.Map;

/**
 * 支付服务
 * 
 */
public interface PayService {

	/**
	 * 支付
	 */
	public PayResult pay(Pay pay);

	/**
	 * 查询
	 */
	public Map<String, String> query(Query query);

	/**
	 * 回调验证
	 */
	public ValidateResult validate(Object object);

	/**
	 * 退款
	 */
	public String refund(Refund refund);

}
