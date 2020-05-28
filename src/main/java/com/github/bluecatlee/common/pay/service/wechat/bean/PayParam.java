package com.github.bluecatlee.common.pay.service.wechat.bean;

import lombok.Data;

/**
 * 微信支付统一下单
 *
 */
@Data
public class PayParam {
	
	//字段名	变量名	必填	类型	示例值	描述
	
	/**
	 * 公众账号ID	appid	是	String(32)	wxd678efh567hg6787	微信支付分配的公众账号ID（企业号corpid即为此appId）
	 */
	private String appid;
	
	/**
	 * 商户号	mch_id	是	String(32)	1230000109	微信支付分配的商户号
	 */
	private String mchId;
	
	/**
	 *设备号	device_info	否	String(32)	013467007045764	自定义参数，可以为终端设备号(门店号或收银设备ID)，PC网页或公众号内支付可以传"WEB" 
	 */
	private String deviceInfo;
	
	/**
	 * 随机字符串	nonce_str	是	String(32)	5K8264ILTKCH16CQ2502SI8ZNMTM67VS	随机字符串，长度要求在32位以内。推荐随机数生成算法
	 */
	private String nonceStr;
	
	/**
	 *签名	sign	是	String(32)	C380BEC2BFD727A4B6845133519F3AD6	通过签名算法计算得出的签名值 
	 */
	private String sign;
	
	/**
	 * 签名类型	sign_type	否	String(32)	MD5	签名类型，默认为MD5，支持HMAC-SHA256和MD5。
	 */
	private String signType;
	
	/**
	 * 商品描述	body	是	String(128)	腾讯充值中心-QQ会员充值	商品简单描述
	 */
	private String body;
	
	/**
	 * 商品详情	detail	否	String(6000)	 	商品详细描述
	 */
	private String detail;
	
	/**
	 * 附加数据	attach	否	String(127)	深圳分店	附加数据，在查询API和支付通知中原样返回，可作为自定义参数使用。
	 */
	private String attach;
	
	/**
	 * 商户订单号	out_trade_no	是	String(32)	20150806125346	商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|*@ ，且在同一个商户号下唯一。
	 */
	private String outTradeNo;
	
	/**
	 * 标价币种	fee_type	否	String(16)	CNY	符合ISO 4217标准的三位字母代码，默认人民币：CNY
	 */
	private String feeType;
	
	/**
	 * 标价金额	total_fee	是	Int	88	订单总金额，单位为分
	 */
	private String totalFee;
	
	/**
	 * 终端IP	spbill_create_ip	是	String(16)	123.12.12.123	APP和网页支付提交用户端ip，Native支付填调用微信支付API的机器IP。
	 */
	private String spbillCreateIp;
	
	/**
	 * 交易起始时间	time_start	否	String(14)	20091225091010	订单生成时间，格式为yyyyMMddHHmmss，如2009年12月25日9点10分10秒表示为20091225091010。
	 */
	private String timeStart;
	
	/**
	 * 交易结束时间	time_expire	否	String(14)	20091227091010	 订单失效时间，格式为yyyyMMddHHmmss，如2009年12月27日9点10分10秒表示为20091227091010。
	 */
	private String timeExpire;
	
	/**
	 * 订单优惠标记	goods_tag	否	String(32)	WXG	订单优惠标记，使用代金券或立减优惠功能时需要的参数
	 */
	private String goodsTag;
	
	/**
	 * 通知地址	notify_url	是	String(256)	http://www.weixin.qq.com/wxpay/pay.php	异步接收微信支付结果通知的回调地址，通知url必须为外网可访问的url，不能携带参数。
	 */
	private String notifyUrl;
	
	/**
	 * 交易类型	trade_type	是	String(16)	JSAPI	取值如下：JSAPI，NATIVE，APP等
	 */
	private String tradeType;
	
	/**
	 * 商品ID	product_id	否	String(32)	12235413214070356458058	trade_type=NATIVE时（即扫码支付），此参数必传。此参数为二维码中包含的商品ID，商户自行定义
	 */
	private String productId;
	
	/**
	 * 指定支付方式	limit_pay	否	String(32)	no_credit	上传此参数no_credit--可限制用户不能使用信用卡支付
	 */
	private String limitPay;
	
	/**
	 * 用户标识	openid	否	String(128)	oUpF8uMuAJO_M2pxb1Q9zNjWeS6o	trade_type=JSAPI时（即公众号支付），此参数必传，此参数为微信用户在商户对应appid下的唯一标识
	 */
	private String openid;
	
	/**
	 * 场景信息	scene_info	否	String(256)	
		{"store_info" : {"id": "SZTX001","name": "腾大餐厅","area_code": "440305","address": "科技园中一路腾讯大厦" }}
		该字段用于上报场景信息，目前支持上报实际门店信息。该字段为JSON对象数据，对象格式为{"store_info":{"id": "门店ID","name": "名称","area_code": "编码","address": "地址" }} 
		
		详细说明：
			-门店id	id	否	String(32)	SZTX001	门店唯一标识
			-门店名称	name	否	String(64)	腾讯大厦腾大餐厅	门店名称
			-门店行政区划码	area_code	否	String(6)	440305	门店所在地行政区划码，详细见《最新县及县以上行政区划代码》
			-门店详细地址	address	否	String(128)	科技园中一路腾讯大厦	门店详细地址
	 */
	private String sceneInfo;
}
