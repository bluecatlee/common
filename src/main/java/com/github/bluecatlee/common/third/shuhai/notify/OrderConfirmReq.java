package com.github.bluecatlee.common.third.shuhai.notify;

import lombok.Data;

@Data
public class OrderConfirmReq {

	private String data;
	
	private String orderId;
	
	private String omsOrderNos;        // 这个字段应该没用 orderId表示蜀海订单号
	
	private String customerOrderId;    // 客户订单号
	
	private String error;
}
