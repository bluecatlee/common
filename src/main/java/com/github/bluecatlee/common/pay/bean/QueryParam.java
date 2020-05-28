package com.github.bluecatlee.common.pay.bean;

import com.github.bluecatlee.common.pay.service.shengfutong.SignParam;
import lombok.Data;

@Data
public class QueryParam {
	
	/**
	 * 版本名称	ServiceCode	是	String(32)	版本名称,默认属性值为: QUERY_ORDER_REQUEST
	 */
	@SignParam(value = 1, name = "ServiceCode")
	private String serviceCode = "QUERY_ORDER_REQUEST";
	
	/**
	 * 版本号	Version	是	String(20)	版本号,默认属性值为: V4.3.1.1.1
	 */
	@SignParam(value = 2, name = "Version")
	private String version = "V4.3.1.1.1";
	
	/**
	 * 字符集	Charset	是	String(10)	字符集,支持GBK、UTF-8、GB2312,默认属性值为:UTF-8
	 */
	@SignParam(value = 3, name = "Charset")
	private String charset = "UTF-8";
	
	/**
	 * 发送方标识	SenderId	是	String(64)	由盛付通提供,默认为:商户号(由盛付通提供的6位正整数)
	 */
	@SignParam(value = 4, name = "SenderId")
	private String senderId;    
	
	/**
	 * 发送支付请求时间	SendTime 是	String(14)	商户网站提交查询请求,必须为14位正整数数字,格式为:yyyyMMddHHmmss,如:20110707112233
	 */
	@SignParam(value = 5, name = "SendTime")
	private String sendTime;    
	
	/**
	 * 商户号	MerchantNo	是	String(50)	用户商户号
	 */
	@SignParam(value = 6, name = "MerchantNo")
	private String merchantNo;  
	
	/**
	 * 商户订单号	OrderNo	是	String(14)	商户订单号,50个字符内、只允许使用数字、字母,确保在商户系统唯一
	 */
	@SignParam(value = 7, name = "OrderNo")
	private String orderNo;
	
	/**
	 * 盛付通交易号	TransNo	否	String(14)	盛付通交易号
	 */
	@SignParam(value = 8, name = "TransNo")
	private String transNo;
	
	/**
	 * 扩展1	Ext1	否	String(128)	英文或中文字符串 支付完成后，按照原样返回给商户
	 */
	@SignParam(value = 9, name = "Ext1")
	private String ext1;
	
	/**
	 * 签名类型	SignType	是	String(10)	签名类型,如：MD5
	 */
	@SignParam(value = 10, name = "SignType")
	private String signType = "MD5";
	
	/**
	 * 签名串	SignMsg	是	String(1024)	签名结果
	 */
	@SignParam(value = 11, name = "SignMsg")
	private String signMsg;
	
}
