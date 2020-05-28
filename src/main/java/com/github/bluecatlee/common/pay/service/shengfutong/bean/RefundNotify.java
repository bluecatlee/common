package com.github.bluecatlee.common.pay.service.shengfutong.bean;

import com.github.bluecatlee.common.pay.service.shengfutong.SignParam;
import lombok.Data;

@Data
public class RefundNotify {
	
	/**
	 * 版本名称	ServiceCode	是	String(32)	版本名称,默认属性值为: REFUND_REP
	 */
	@SignParam(value = 1, name = "ServiceCode")
	private String serviceCode = "REFUND_REQ";
	
	/**
	 * 版本号	Version	是	String(20)	版本号,默认属性值为: V4.4.2.2.1
	 */
	@SignParam(value = 2, name = "Version")
	private String version = "V4.4.2.2.1";
	
	/**
	 * 字符集	Charset	是	String(10)	字符集,支持GBK、UTF-8、GB2312,默认属性值为:UTF-8
	 */
	@SignParam(value = 3, name = "Charset")
	private String charset = "UTF-8";
	
	/**
	 * 请求序列号	TraceNo	是	String(40)	报文发起方唯一消息标识
	 */
	@SignParam(value = 4, name = "TraceNo")
	private String traceNo;    
	
	/**
	 * 发送方标识	SenderId	是	String(64)	由盛付通SFT
	 */
	@SignParam(value = 5, name = "SenderId")
	private String senderId;    
	
	/**
	 *发送支付请求时间	SendTime	是	String(14)	商户网站提交查询请求,必须为14位正整数数字,格式为:yyyyMMddHHmmss,如:20110707112233
	 */
	@SignParam(value = 6, name = "SendTime")
	private String sendTime;
	
	/**
	 * 商户退款订单号/请求号	RefundOrderNo	是	String(14)	商户订单号,50个字符内、只允许使用数字、字母,确保在商户系统唯一
	 */
	@SignParam(value = 7, name = "RefundOrderNo")
	private String refundOrderNo;
	
	/**
	 * 商户原始订单号	OriginalOrderNo	是	String(50)	商户原始订单号
	 */
	@SignParam(value = 8, name = "OriginalOrderNo")
	private String originalOrderNo;
	
	/**
	 *退款状态	Status	是	String(5)	00:处理中;01：成功;02:失败;
	 */
	@SignParam(value = 9, name = "Status")
	private String status;
	
	/**
	 * 退款金额	RefundAmount	是	String(14)	单位“元”，两位小数
	 */
	@SignParam(value = 10, name = "RefundAmount")
	private String refundAmount;
	
	/**
	 * 盛付通退款订单号	RefundTransNo	是	String(50)	盛付通退款订单号
	 */
	@SignParam(value = 11, name = "RefundTransNo")
	private String refundTransNo;
	
	/**
	 * 扩展1	Ext1	否	String(128)	英文或中文字符串 支付完成后，按照原样返回给商户
	 */
	@SignParam(value = 12, name = "Ext1")
	private String ext1;
	
	/**
	 * 签名类型	SignType	是	String(10)	签名类型,如：MD5
	 */
	@SignParam(value = 13, name = "SignType")
	private String signType;
	
	/**
	 * 签名串	SignMsg	是	String(1024)	签名结果
	 */
	@SignParam(value = -1, name = "SignMsg")
	private String signMsg;
	
}
