package com.github.bluecatlee.common.pay.factory;

import com.github.bluecatlee.common.pay.service.PayService;

public abstract class AbstractPayFactory {

	/**
	 * 创建支付服务类
	 * 
	 * @param type
	 *            类型,对应配置的pay_id
	 *            <ul>
	 *            <li>1:支付宝</li>
	 *            <li>10:盛付通</li>
	 *            <li>3:微信h5</li>
	 *            <li>13:微信pc</li>
	 *            <ul>
	 * @return
	 */
	public abstract PayService create(String type);

}
