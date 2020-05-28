package com.github.bluecatlee.common.third.shuhai.req;

import java.util.List;

import lombok.Data;

@Data
public class Stall {
	
	/**
	 * 档口编码,长度不超过10字符或20个字节 必填
	 */
	private String stallCode;
	
	/**
	 * 客户门店编码 必填
	 */
	private String shopCode;
	
	/**
	 * 要求交货日期，格式为yyyy-MM-dd
	 */
	private String deliveryDate;

	/**
	 * 商品集合
	 */
	private List<Product> products;
	
}
