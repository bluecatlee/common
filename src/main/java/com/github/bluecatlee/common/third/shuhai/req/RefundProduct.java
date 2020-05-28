package com.github.bluecatlee.common.third.shuhai.req;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class RefundProduct {
	
	/**
	 * 客户商品编号 必填
	 */
	private String productId;
	
	/**
	 * 下单数量（小数2位精度）必填   在退货业务中表示退货数量
	 */
	private BigDecimal quantity;

}
