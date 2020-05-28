package com.github.bluecatlee.common.pay.bean;

import lombok.Data;

@Data
public class QueryRefundParam {
	/**
     * 公众账号ID	appid	是	String(32)	wx8888888888888888	微信分配的公众账号ID（企业号corpid即为此appId）
     */
    private String appid;
    
    /**
     * 商户号	mch_id	是	String(32)	1900000109	微信支付分配的商户号
     */
    private String mchId;
    
    /**
     * 微信订单号	transaction_id	四选一	String(32)	1217752501201407033233368018	微信生成的订单号，建议优先使用
     */
    private String transactionId;    
    
    /**
     * 商户订单号	out_trade_no  四选一	String(32)	1217752501201407033233368018	商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|*@ ，且在同一个商户号下唯一。
     * 			商户系统内部的订单号,transaction_id 、out_trade_no 二选一，如果同时存在优先级：transaction_id>out_trade_no
     */
    private String outTradeNo; 
    
    /**
     * 随机字符串	nonce_str	是	String(32)	5K8264ILTKCH16CQ2502SI8ZNMTM67VS	随机字符串，不长于32位。
     */
    private String nonceStr;
    
    /**
     * 签名	sign	是	String(32)	C380BEC2BFD727A4B6845133519F3AD6	签名
     */
    private String sign;
    
    /**
     * 签名类型	sign_type	否	String(32)	HMAC-SHA256	签名类型，目前支持HMAC-SHA256和MD5，默认为MD5
     */
    private String signType;
    
    /**
     * 商户退款单号	out_refund_no  四选一	 String(64)	1217752501201407033233368018	商户系统内部的退款单号，商户系统内部唯一，只能是数字、大小写字母_-|*@ ，同一退款单号多次请求只退一笔。
     */
    private String outRefundNo;
    
    /**
     * 微信退款单号	refund_id  四选一   	String(32)	1217752501201407033233368018	 微信生成的退款单号，在申请退款接口有返回
     */
    private String refundId;
    
    /**
     * 偏移量	offset	否	Int	15	 偏移量，当部分退款次数超过10次时可使用，表示返回的查询结果从这个偏移量开始取记录
     */
    private String offset;
    
}
