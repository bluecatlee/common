package com.github.bluecatlee.common.third.shuhai.service;

import com.github.bluecatlee.common.third.shuhai.req.SHOrder;
import com.github.bluecatlee.common.third.shuhai.utils.ShuhaiUtils;
import org.springframework.stereotype.Service;

/**
 * 蜀海service
 * @author Bluecat lee
 *
 */
@Service("shuhaiService")
@Deprecated
public class ShuhaiService {

	public boolean createShuhaiOrder(SHOrder shOrder) {
		
		// 需要参数 业务订单号 业务方商品/货品id 规则名称 数量
		
		// 校验是否已经生成过蜀海订单 调用蜀海查询订单接口
		SHOrder orderDetail = ShuhaiUtils.getOrderDetail(shOrder.getCustomerOrderId());
		if (orderDetail == null) {
			
		} else {
			// 已经创建蜀海订单 不要重复创建
			return true;
		}
		
		// 创建蜀海销售订单 测试
//		SHOrder shorder = new SHOrder();
//		shorder.setCustomerOrderId(orderCode);
//		List<Shop> shops = new ArrayList<>();
//		Shop shop = new Shop();
//		shop.setShopCode("0W50000005");
////		shop.setDeliveryDate("2019-12-12");
//		Product product = new Product();
//		product.setProductId("4703710");
//		product.setQuantity(new BigDecimal("1"));
//		product.setSpec("测试规格");
//		
//		Product product2 = new Product();
//		product2.setProductId("02020003");
//		product2.setQuantity(new BigDecimal("1"));
//		product2.setSpec("测试规格");
//		
//		List<Product> products = new ArrayList<>();
//		products.add(product);
//		products.add(product2);
//		
//		shop.setProducts(products);
//		shops.add(shop);
//		shorder.setShops(shops);
		
		return ShuhaiUtils.createShopOrder(shOrder);
		
	}
	
}
