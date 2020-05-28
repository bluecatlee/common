package com.github.bluecatlee.common.pay.bean;

import com.github.bluecatlee.common.pay.service.shengfutong.SignParam;
import lombok.Data;

@Data
public class RefundParam {
	
	/**
	 * 版本名称	ServiceCode	是	String(32)	版本名称,默认属性值为: REFUND_REQ
	 */
	@SignParam(value = 1, name = "ServiceCode")
	private String serviceCode = "REFUND_REQ";
	
	/**
	 * 版本号	Version	是	String(20)	版本号,默认属性值为: V4.4.1.1.1
	 */
	@SignParam(value = 2, name = "Version")
	private String version = "V4.4.1.1.1";
	
	/**
	 * 字符集	Charset	是	String(10)	字符集,支持GBK、UTF-8、GB2312,默认属性值为:UTF-8
	 */
	@SignParam(value = 3, name = "Charset")
	private String charset = "UTF-8";
	
	/**
	 * 发送方标识	SenderId	是	String(64)	由盛付通提供,默认为:商户号(由盛付通提供的8位正整数)
	 */
	@SignParam(value = 4, name = "SenderId")
	private String senderId;    
	
	/**
	 * 发送支付请求时间	SendTime 是	String(14)	商户网站提交查询请求,必须为14位正整数数字,格式为:yyyyMMddHHmmss,如:20110707112233
	 */
	@SignParam(value = 5, name = "SendTime")
	private String sendTime;    
	
	/**
	 * 商户号	MerchantNo	是	String(50)	由盛付通提供,默认为:商户号(由盛付通提供的6位正整数)
	 */
	@SignParam(value = 6, name = "MerchantNo")
	private String merchantNo;  
	
	/**
	 * 商户退款订单号	RefundOrderNo	是	String(14)	退款订单号/请求号，商户自定义生成，需保持订单的唯一性
	 */
	@SignParam(value = 7, name = "RefundOrderNo")
	private String refundOrderNo;
	
	/**
	 * 商户原始订单号	OriginalOrderNo	是	String(50)	商户原始订单号
	 */
	@SignParam(value = 8, name = "OriginalOrderNo")
	private String originalOrderNo;
	
	/**
	 * 退款金额	RefundAmount	是	String(14)	退款金额,必须大于0,包含2位小数  如：RefundAmount =1.00
	 */
	@SignParam(value = 9, name = "RefundAmount")
	private String refundAmount;
	
	/**
	 * 退款路由	RefundRoute	否	String(5)	0:退款到原始资金源
	 */
	@SignParam(value = 10, name = "RefundRoute")
	private String refundRoute = "0";
	
	/**
	 * 异步通知地址	NotifyURL	是	String(200)	服务端退款通知结果地址,退款成功后,盛付通将发送退款状态信息至该地址如:http://www.testpay.com/testpay.jsp
	 */
	@SignParam(value = 11, name = "NotifyURL")
	private String notifyURL;
	
	/**
	 * 备注	Memo	否	String(1024)	备注
	 */
	@SignParam(value = 12, name = "Memo")
	private String memo;
	
	/**
	 * 扩展1	Ext1	否	String(128)	英文或中文字符串
	 * 退款完成后，按照原样返回给商户
	 * 如果有同一笔商户订单号生成多笔交易的情况，而商户只想退一笔交易订单，则在扩展字段里面增加：TransNo(盛付通交易号，选填，String(14))
 	 * 样例：TransNo=XXXX;key2=Value2;key3=Value3;
	 */
	@SignParam(value = 13, name = "Ext1")
	private String ext1;
	
	/**
	 * 签名类型	SignType	是	String(10)	签名类型,如：MD5
	 */
	@SignParam(value = 14, name = "SignType")
	private String signType;
	
}
