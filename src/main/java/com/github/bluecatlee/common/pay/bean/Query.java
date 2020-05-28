package com.github.bluecatlee.common.pay.bean;

import java.util.Map;

import lombok.Data;

/**
 * 查询
 */
@Data
public class Query {
	
	/**
	 * 查询类型  pay表示支付查询 refund表示退款查询
	 */
	private String queryType;
	
	/**
     * 公众账号ID	
     */
    private String appid;
    
    /**
     * 商户号	
     */
    private String mchId;
	
	/**
	 * 商户密钥，用于生成签名
	 */
	private String partnerKey;

    /**
     * 扩展字段
     * 订单号/退款单号以及其他非必需字段保存在map中
     * 如果是查询订单,transactionId和outTradeNo二选一,存进map中。可选参数signType
     * 如果是查询退款,transactionId和outTradeNo和refundId和outRefundNo四选一,存进map中。可选参数signType offset
     */
    private Map<String, String> ext;
}
