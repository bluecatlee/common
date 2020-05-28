package com.github.bluecatlee.common.third.shuhai.notify;

import com.github.bluecatlee.common.third.shuhai.req.RefundProduct;
import lombok.Data;

import java.util.List;

@Data
public class RefundOrderConfirm {
	
	private String orderId;
	
	private String shopCode;
	
	private String refundOrderId;
	
	private String refundDate;
	
	private String refundType;
	
	private List<RefundProduct> items;

}
