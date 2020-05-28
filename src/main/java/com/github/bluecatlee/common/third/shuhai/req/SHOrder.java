package com.github.bluecatlee.common.third.shuhai.req;

import java.util.List;

import lombok.Data;

@Data
public class SHOrder {
	
	/**
	 * 客户ENT码 必填
	 */
	private String ent;
	
	/**
	 * 客户编号
	 */
	private String customerId;
	
	/**
	 * 客户销售订单编号 必填
	 */
	private String customerOrderId;
	
	/**
	 * 1：代采，2：代仓(一个单号内物料要么是代仓要么是代采，不可代仓代采混合) 必填
	 */
	private String bizType;
	
	/**
	 * SKU或者SPU 必填
	 */
	private String productType;
	
	/**
	 * 门店订单必填
	 */
	private List<Shop> shops;
	
	/**
	 * 档口订单必填
	 */
	private List<Stall> stalls;
	
}
