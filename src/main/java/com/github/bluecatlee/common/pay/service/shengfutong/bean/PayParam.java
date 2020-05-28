package com.github.bluecatlee.common.pay.service.shengfutong.bean;

import com.github.bluecatlee.common.pay.service.shengfutong.SignParam;
import lombok.Data;

@Data
public class PayParam {

	/**
	 * 版本名称 Name 是 String(32) 版本名称,默认属性值为:B2CPayment
	 */
	@SignParam(value = 1, name = "Name")
	private String name = "B2CPayment";

	/**
	 * 版本号 Version 是 String(20) 版本号,默认属性值为: V4.1.1.1.1
	 */
	@SignParam(value = 2, name = "Version")
	private String version = "V4.1.1.1.1";

	/**
	 * 字符集 Charset 是 String(10) 字符集,支持GBK、UTF-8、GB2312,默认属性值为:UTF-8
	 */
	@SignParam(value = 3, name = "Charset")
	private String charset = "UTF-8";

	/**
	 * 发送方标识 MsgSender 是 String(64) 由盛付通提供,默认为:商户号(由盛付通提供的8位正整数),用于盛付通判别请求方的身份
	 */
	@SignParam(value = 4, name = "MsgSender")
	private String msgSender;
	
	/**
	 * 商户平台会员号OutMemberId 是 String(32)	商户提供：商户系统内针对会员的唯一标识（用于查询该会员在盛付通已绑定的银行卡）
	 */
	@SignParam(value = 5, name = "OutMemberId")
	private String outMemberId;

	/**
	 * 发送支付请求时间 SendTime 否 String(14) 防钓鱼时间戳，获取方式见6.1.8，非必填参数
	 */
	@SignParam(value = 6, name = "SendTime")
	private String sendTime;

	/**
	 * 商户订单号 OrderNo 是 String(50) 商户订单号,50个字符内、只允许使用数字、字母,确保在商户系统唯一，商户订单号不能重复
	 */
	@SignParam(value = 7, name = "OrderNo")
	private String orderNo;

	/**
	 * 支付金额 OrderAmount 是 String(14) 支付金额,必须大于0,包含2位小数 如：OrderAmount=1.00
	 */
	@SignParam(value = 8, name = "OrderAmount")
	private String orderAmount;

	/**
	 * 商户订单提交时间 OrderTime 是 String(14)
	 * 商户提交用户订单时间,必须为14位正整数数字,格式为:yyyyMMddHHmmss,如:OrderTime=20110808112233
	 */
	@SignParam(value = 9, name = "OrderTime")
	private String orderTime;

	/**
	 * 货币类型 Currency 否 String(10) 英文的币种代码, 如为空则默认为人民币，目前支持的币种代码为： 人民币:CNY 美元: USD
	 * 英镑: GBP 港币: HKD 新加坡元:SGD 日元: JPY 加拿大元:CAD 澳元:AUD 欧元:EUR 瑞士法郎:CHF
	 */
	@SignParam(value = 10, name = "Currency")
	private String currency = "CNY";

	/**
	 * 支付类型编码 PayType 否 String(10) 见附录7.1.1支付类型 如:PayType=PT001 为空时默认显示合同规定的全部渠道
	 */
	@SignParam(value = 11, name = "PayType")
	private String payType = "PT001";

	/**
	 * 支付渠道 PayChannel 否 String(14) 支付渠道，当指定PayType 为 PT001网银直连支付模式时有效（19 储蓄卡，20 信用卡
	 * 12企业网银）
	 */
	@SignParam(value = 12, name = "PayChannel")
	private String payChannel = "19";

	/**
	 * 银行编码 InstCode 否 String(256) 见附录7.1.2综合网银编码列表,机构代码列表以逗号分隔,如：InstCode=ICBC
	 */
	@SignParam(value = 13, name = "InstCode")
	private String instCode;

	/**
	 * 支付成功后客户端浏览器回调地址 PageUrl 是 String(256)
	 * 客户端浏览器回调地址,支付成功后,将附带回调数据跳转到此页面,商户可以进行相关处理并显示给终端用户,如:http://www.testpay.com/testpay.jsp
	 * 
	 */
	@SignParam(value = 14, name = "PageUrl")
	private String pageUrl;

	/**
	 * 服务端通知发货地址 NotifyUrl 是 String(256)
	 * 服务端通知发货地址,支付成功后,盛付通将发送“支付成功”信息至该地址,通知商户发货,商户收到信息后,需返回相应信息至盛付通,表明已收到发货通知。返回信息只能定义为“OK”（注意为大写英文字母）,返回其他信息均为失败,如:http://www.testpay.com/testpay.jsp
	 * 
	 */
	@SignParam(value = 15, name = "NotifyUrl")
	private String notifyUrl;

	/**
	 * 在收银台跳转到商户指定的地址 BackUrl 否 String(256)
	 * 为了让客户提交订单到收银台后，能随时返回到商户页面，在收银台首页及其他个别页面，放置了返回商户页面的按钮，点击即跳转到商户对该字段指定的地址。如果商户没有指定该字段的值，则收银台不会显示返回商户页面的按钮；如果商户指定了该页面的地址，则收银台显示返回商户页面的按钮，点击跳转到商户指定的页面。
	 * 
	 */
	@SignParam(value = 16, name = "BackUrl")
	private String backUrl;

	/**
	 * 商品名称 ProductName 否 String(56) 商品名称,如:ProductName=测试商品test
	 * 
	 */
	@SignParam(value = 17, name = "ProductName")
	private String productName;

	/**
	 * 支付人联系方式 BuyerContact 否 String(256) 字符串，手机号码或者邮箱地址
	 */
	@SignParam(value = 18, name = "BuyerContact")
	private String buyerContact;

	/**
	 * 买家IP地址 BuyerIp 是 String(20) 防钓鱼用,买家的ip地址
	 */
	@SignParam(value = 19, name = "BuyerIp")
	private String buyerIp;

	/**
	 * 真实姓名 realName 否 String(30) 针对跨境商户，实名认证时所需字段
	 */
	@SignParam(value = -1, name = "realName")
	private String realName;

	/**
	 * 身份证号 idNo 否 String(20) 针对跨境商户，实名认证时所需字段
	 */
	@SignParam(value = -1, name = "idNo")
	private String idNo;

	/**
	 * 手机号 mobile 否 String(11) 实名认证时所需字段
	 */
	@SignParam(value = -1, name = "mobile")
	private String mobile;

	/**
	 * 扩展1 Ext1 否 String(128) 英文或中文字符串 支付完成后，按照原样返回给商户
	 */
	@SignParam(value = 20, name = "Ext1")
	private String ext1;

	/**
	 * 签名类型 SignType 是 String(10) 签名类型,如：MD5
	 */
	@SignParam(value = 21, name = "SignType")
	private String signType = "MD5";

}