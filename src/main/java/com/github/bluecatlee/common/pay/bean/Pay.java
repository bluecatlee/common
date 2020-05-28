package com.github.bluecatlee.common.pay.bean;

import java.util.Map;
import lombok.Data;

/**
 * 支付参数,非通用字段保存在map中
 */
@Data
public class Pay {
	
	/**
	 * 支付类型
	 */
	private String type;
	
	/**
	 * 商户号
	 */
	private String partner;
	
	/**
	 * 密钥
	 */
	private String partnerKey;
	
	/**
	 * 异步回调地址
	 */
	private String notifyUrl;
	
	/**
	 * 订单号
	 */
	private String outTradeNo;

	/**
	 * 付款金额
	 */
	private String amount;

	/**
	 * 商品信息body
	 */
	private String productName;

	/**
	 * 购买人信息 如登录用户名称、编号等
	 */
	private String outMemberId;   

	/**
	 * 页面同步回调地址
	 */
	private String pageUrl;

	/**
	 * ip地址
	 */
	private String ip;
	
	/**
	 * 其他字段,存放不同支付方式的其他参数,对应接口中需要的参数进行设值
	 */
	private Map<String, String> ext;
	
}
