package com.github.bluecatlee.common.pay.bean;

import com.github.bluecatlee.common.pay.service.shengfutong.SignParam;
import lombok.Data;

@Data
public class PayNotify {

	/**
	 * 版本名称 Name 是 String(32) 版本名称,默认属性值为:B2CPayment
	 */
	@SignParam(value = 1, name = "Name")
	private String name;

	/**
	 * 版本号 Version 是 String(20) 版本号,默认属性值为: V4.1.1.1.1
	 */
	@SignParam(value = 2, name = "Version")
	private String version;

	/**
	 * 字符集 Charset 是 String(10) 字符集,支持GBK、UTF-8、GB2312,默认属性值为:UTF-8
	 */
	@SignParam(value = 3, name = "Charset")
	private String charset;

	/**
	 * 发送方标识 MsgSender 是 String(64) 由盛付通提供,默认为:商户号(由盛付通提供的8位正整数),用于盛付通判别请求方的身份
	 */
	@SignParam(value = 5, name = "MsgSender")
	private String msgSender;

	/**
	 * 发送支付请求时间 SendTime 否 String(14) 防钓鱼时间戳，获取方式见6.1.8，非必填参数
	 */
	@SignParam(value = 6, name = "SendTime")
	private String sendTime;

	/**
	 * 商户订单号 OrderNo 是 String(50) 商户订单号,50个字符内、只允许使用数字、字母,确保在商户系统唯一，商户订单号不能重复
	 */
	@SignParam(value = 8, name = "OrderNo")
	private String orderNo;

	/**
	 * 支付金额 OrderAmount 是 String(14) 支付金额,必须大于0,包含2位小数 如：OrderAmount=1.00
	 */
	@SignParam(value = 9, name = "OrderAmount")
	private String orderAmount;

	/**
	 * 银行编码 InstCode 否 String(256) 见附录7.1.2综合网银编码列表,机构代码列表以逗号分隔,如：InstCode=ICBC
	 */
	@SignParam(value = 7, name = "InstCode")
	private String instCode;

	/**
	 * 扩展1 Ext1 否 String(128) 英文或中文字符串 支付完成后，按照原样返回给商户
	 */
	@SignParam(value = 20, name = "Ext1")
	private String ext1;

	/**
	 * 签名类型 SignType 是 String(10) 签名类型,如：MD5
	 */
	@SignParam(value = 21, name = "SignType")
	private String signType;

	/**
	 * 请求序列号 TraceNo 是 String(40) 报文发起方唯一消息标识 盛付通交易号 TraceNo 是 String(40)
	 * 盛付通系统的交易号,商户只需记录
	 */
	@SignParam(value = 4, name = "TraceNo")
	private String traceNo;

	/**
	 * 盛付通交易号 TransNo 是 String(40) 盛付通系统的交易号,商户只需记录
	 */
	@SignParam(value = 10, name = "TransNo")
	private String transNo;

	/**
	 * 盛付通实际支付金额 TransAmount 是 String(14) 用户实际支付金额
	 */
	@SignParam(value = 11, name = "TransAmount")
	private String transAmount;

	/**
	 * 支付状态 TransStatus 是 String(10) 见附录 7.3.1 支付状态
	 */
	@SignParam(value = 12, name = "TransStatus")
	private String transStatus;

	/**
	 * 盛付通交易类型 TransType 是 String(10) 见附录 7.1.1 支付类型
	 */
	@SignParam(value = 13, name = "TransType")
	private String transType;

	/**
	 * 盛付通交易时间 TransTime 是 String(14)
	 * 用户通过商户网站完成交易订单的时间,必须为14位正整数数字,格式为:yyyyMMddHHmmss,如:20110707112233
	 * 
	 */
	@SignParam(value = 14, name = "TransTime")
	private String transTime;

	/**
	 * 商户号 MerchantNo 是 String(64) 商户号
	 */
	@SignParam(value = 15, name = "MerchantNo")
	private String merchantNo;

	/**
	 * 错误代码 ErrorCode 是 String(256) 商户交易错误代码
	 */
	@SignParam(value = 16, name = "ErrorCode")
	private String errorCode;

	/**
	 * 错误消息 ErrorMsg 是 String(256) 商户交易错误消息
	 */
	@SignParam(value = 17, name = "ErrorMsg")
	private String errorMsg;
	
	/**
	 * 支付状态 paymentStatus	是	String(256)	根据商户业务配置返回的
	 */
	@SignParam(value = 18, name = "paymentStatus")
	private String paymentStatus;
	
	/**
	 * 结果类型	resultType	是	String(256)	根据商户业务配置返回的
	 */
	@SignParam(value = 19, name = "resultType")
	private String resultType;

	/**
	 * 网银流水号 BankSerialNo 是 String(64) 银行返回的交易流水号
	 */
	@SignParam(value = -1, name = "BankSerialNo")
	private String bankSerialNo;

	/**
	 * 请求币种 RequestCurrency 否 String(10) 请求时传入的币种，请求币种为外币时不为空。如: USD
	 * 
	 */
	@SignParam(value = -1, name = "RequestCurrency")
	private String requestCurrency;

	/**
	 * 支付币种 PaymentCurrency 否 String(10) 请求币种为外币时不为空，支付时的币种，目前都为人民币: CNY.
	 * 
	 */
	@SignParam(value = -1, name = "PaymentCurrency")
	private String paymentCurrency;

	/**
	 * 请求订单金额 RequestOrderAmount 否 String(14) 请求的订单金额, 请求币种为外币时不为空,如：12.00
	 * 
	 */
	@SignParam(value = -1, name = "RequestOrderAmount")
	private String requestOrderAmount;

	/**
	 * 汇率 FexchangeRate 否 String(14) 汇率，请求币种为外币时不为空，如：6.1593
	 * 
	 */
	@SignParam(value = -1, name = "FexchangeRate")
	private String fexchangeRate;

	/**
	 * 签名串 SignMsg 是 String(14) 签名结果
	 */
	@SignParam(value = -1, name = "SignMsg")
	private String signMsg;

}
