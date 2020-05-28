package com.github.bluecatlee.common.third.shuhai.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bluecatlee.common.third.shuhai.req.SHOrder;
import com.github.bluecatlee.common.third.shuhai.resp.Response;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 蜀海工具类
 * @author bluecat lee
 *
 */
@SuppressWarnings("all")
public class ShuhaiUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(ShuhaiUtils.class);
	
	private static final ObjectMapper mapper = new ObjectMapper();
	
	private static final String TAG = "蜀海 >>> ";
	
	private static final String default_content_type = "application/json;charset=utf-8";
	private static final String sign_type = "Md5";
	
	private static final String createOrderUri = "platformApi/v1/order/sale/receive";
	private static final String getOrderDetailUri = "platformApi/v1/order/sale/get";
	private static final String queryOrderUri = "platformApi/v1/order/sale/query";
	private static final String getSendQuantityUri = "platformApi/v1/order/sale/getSendQuantity";
	private static final String getRefundOrderUri = "platformApi/v1/order/sale/refund/get";
	private static final String queryRefundOrderUri = "platformApi/v1/order/sale/refund/query";  
	private static final String syncActualReceiveUri = "platformApi/v1/order/sale/syncActualReceive";  
	
	// 配置信息
	private static String ENT; // 客户ENT码
	private static String APIKEY; // APIKEY
	private static String SECRET; // SECRET
	private static String API;
	
	static {
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES); // bean中不存在对应字段时 忽略
		
		// 生产走配置文件的配置 如果采用Spring Boot，可以直接在application.yml中配置
		Properties properties = getConfigProperties();
		if (properties != null) {
			ENT = (String) properties.get("shuhai.ent"); 
			APIKEY = (String) properties.get("shuhai.apikey"); 
			SECRET = (String) properties.get("shuhai.secret"); 
			API = (String) properties.get("shuhai.api"); 
		}
		
		// 本地测试默认配置
		if (StringUtils.isBlank(ENT)) {
			ENT = "yegoo";
		}
		if (StringUtils.isBlank(APIKEY)) {
			APIKEY = "SHSC201912045714";
		}
		if (StringUtils.isBlank(SECRET)) {
			SECRET = "63640470009856000";
		}
		if (StringUtils.isBlank(API)) {
			API = "http://testopengateway.shuhaisc.com";
		}
		
	}

	
	// 销售订单接单接口 门店模式
	// 返回值为true只是代表接口调用成功 不代表蜀海订单创建成功 
	// 蜀海订单是否创建成功通过回调确定 或者主动调用蜀海订单详情接口判断
	public static boolean createShopOrder(SHOrder order) {
		String orderCode = order.getCustomerOrderId(); // 客户订单号
		
		order.setEnt(ENT);
		order.setBizType("1");              // 1：代采，2：代仓(一个单号内物料要么是代仓要么是代采，不可代仓代采混合)
		order.setProductType("SPU");        // 类型 SKU/SPU
		
		String request = "";
		try {
			request = mapper.writeValueAsString(order);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("参数格式不正确: 转成json失败");
		}
		logger.debug(TAG + "业务订单{} 创建蜀海销售订单参数 {}", orderCode, request);
		
		String response = ShuhaiHttpUtils.postJson(API, request, header(createOrderUri));
		logger.debug(TAG + "业务订单{} 创建蜀海销售订单响应结果 {}", orderCode, response);
		
		try {
			Response<String> result = mapper.readValue(response, new TypeReference<Response<String>>() {});
			if (result.getStatus().intValue() != 200) {
				return false;
			}
			String orderCodeR = result.getData();
			if (StringUtils.equals(orderCode, orderCodeR)) { // 返回原订单编号
				return true;
			} else {
				logger.error(TAG + "业务订单{} 创建销售订单后返回的原订单号[{}]不匹配 ", orderCode, orderCodeR);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return false;
		
	}
	
	// 根据客户销售订单编号获取订单详情
	public static SHOrder getOrderDetail(String orderCode) {
		Map<String, Object> params = new HashMap<>();
		params.put("ent", ENT); 
		params.put("thirdOrderId", orderCode);
		
		String request = "";
		try {
			request = mapper.writeValueAsString(params);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("参数格式不正确: 转成json失败");
		}
		String response = ShuhaiHttpUtils.postJson(API, request, header(getOrderDetailUri));
		
		try {
			Response<List<SHOrder>> result = mapper.readValue(response, new TypeReference<Response<List<SHOrder>>>() {});
			return result.getData().get(0);
		} catch (Exception e) {
//			e.printStackTrace();
		} 
		return null;
		
	}
	
	// 按时间段查询销售订单 
	public static List<SHOrder> queryOrders(String startDate, String endDate) {
		// 校验参数 日期跨度不能超过60天
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			long startTime = format.parse(startDate).getTime();
			long endTime = format.parse(endDate).getTime();
			if ((endTime - startTime) / (1000 * 3600 * 24) > 60) {
				throw new RuntimeException("参数不合法: 日期跨度不能超过60天");
			}
		} catch (ParseException e) {
			throw new RuntimeException("日期参数格式不正确");
		}
		
		Map<String, String> params = new HashMap<>();
		params.put("ent", ENT);
		params.put("startDateStr", startDate);
		params.put("endDateStr", endDate);
		
		String request = "";
		try {
			request = mapper.writeValueAsString(params);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("参数格式不正确: 转成json失败");
		}
		
		String response = ShuhaiHttpUtils.postJson(API, request, header(queryOrderUri));
		
		try {
			Response<List<SHOrder>> result = mapper.readValue(response, new TypeReference<Response<List<SHOrder>>>() {});
			return result.getData();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return null;
		
	} 
	
	// 获取实发数量
	public static SHOrder getSendQuantity(String orderCode) {
		Map<String, String> params = new HashMap<>();
		params.put("ent", ENT);
		params.put("thirdOrderId", orderCode);
		
		String request = "";
		try {
			request = mapper.writeValueAsString(params);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("参数格式不正确: 转成json失败");
		}
		
		String response = ShuhaiHttpUtils.postJson(API, request, header(getSendQuantityUri));
		
		try {
			Response<List<SHOrder>> result = mapper.readValue(response, new TypeReference<Response<List<SHOrder>>>() {});
			return result.getData().get(0);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return null;
	}
	
	// 获取销售退货单
	public static SHOrder getRefundOrder(String orderCode) {
		Map<String, String> params = new HashMap<>();
		params.put("ent", ENT);
		params.put("thirdOrderId", orderCode);
		
		String request = "";
		try {
			request = mapper.writeValueAsString(params);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("参数格式不正确: 转成json失败");
		}
		
		String response = ShuhaiHttpUtils.postJson(API, request, header(getRefundOrderUri));
		
		try {
			Response<List<SHOrder>> result = mapper.readValue(response, new TypeReference<Response<List<SHOrder>>>() {});
			return result.getData().get(0);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return null;
	}
	
	// 查询退货单
	public static List<SHOrder> getRefundOrders(String startDate, String endDate) {
		// 校验参数 日期跨度不能超过60天
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			long startTime = format.parse(startDate).getTime();
			long endTime = format.parse(endDate).getTime();
			if ((endTime - startTime) / (1000 * 3600 * 24) > 60) {
				throw new RuntimeException("参数不合法: 日期跨度不能超过60天");
			}
		} catch (ParseException e) {
			throw new RuntimeException("日期参数格式不正确");
		}
		Map<String, String> params = new HashMap<>();
		params.put("ent", ENT);
		params.put("startDateStr", startDate);
		params.put("endDateStr", endDate);
		
		String request = "";
		try {
			request = mapper.writeValueAsString(params);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("参数格式不正确: 转成json失败");
		}
		
		String response = ShuhaiHttpUtils.postJson(API, request, header(queryRefundOrderUri));
		
		try {
			Response<List<SHOrder>> result = mapper.readValue(response, new TypeReference<Response<List<SHOrder>>>() {});
			return result.getData();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return null;
	}
	
	/**
	 * 同步客户实收数量
	 * @param orderCode 客户订单号
	 * @param shopCode 门店编号
	 * @param productId 物料编号
	 * @param actualQuantity 实收数量
	 */
	public static boolean syncActualReceive(String orderCode, String shopCode, String productId, BigDecimal actualQuantity) {
		Map<String, Object> params = new HashMap<>();
		params.put("ent", ENT);
		params.put("customerOrderId", orderCode);
		
		List<Map<String, Object>> shops = new ArrayList<>();
		Map<String, Object> shop = new HashMap<>();
		shop.put("shopCode", shopCode);

		List<Map<String, Object>> products = new ArrayList<>();
		Map<String, Object> product = new HashMap<>();
		product.put("productId", productId);
		product.put("actualQuantity", actualQuantity);
		products.add(product);
		
		shop.put("products", products);
		
		shops.add(shop);
		
		params.put("shops", shops);
		
		String request = "";
		try {
			request = mapper.writeValueAsString(params);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("参数格式不正确: 转成json失败");
		}
		logger.debug(TAG + "业务订单{} 同步实收数量参数 {}", orderCode, request);
		
		String response = ShuhaiHttpUtils.postJson(API, request, header(syncActualReceiveUri));
		logger.debug(TAG + "业务订单{} 同步实收数量响应 {}", orderCode, response);
		try {
			Response<String> result = mapper.readValue(response, new TypeReference<Response<String>>() {});
			if (result.getStatus().intValue() == 200) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return false;
		
	}
	
	
	/**
	 * 生成头部参数
	 * @param method
	 * @return
	 */
	private static Map<String, String> header(String method) {
		Map<String, String> headerMap = new HashMap<>();
	    headerMap.put("method", method);
	    headerMap.put("signType", "Md5");
	    headerMap.put("apiKey", APIKEY);
	    String timestamp = String.valueOf(System.currentTimeMillis());
	    headerMap.put("timestamp", timestamp);
	    headerMap.put("Content-Type", default_content_type);
	    String sign = sign(method, timestamp);
	    headerMap.put("sign", sign);
//	    logger.debug(TAG + "蜀海请求头部参数：" + headerMap);
		return headerMap;
	}
	
	/**
	 * 生成签名值
	 * @param method
	 * @return
	 */
	private static String sign(String method, String timestamp) {
		String signInput = "apiKey=" + APIKEY + "&method=" + method + "&secret=" + SECRET + "&timestamp=" + timestamp;
		
		try {
			String sign = md5(signInput);
			return sign;
		} catch (NoSuchAlgorithmException e) {
		}
		return null;
	}
	
	private static Properties getConfigProperties() {
        Properties properties = new Properties();
        try {
            properties.load(ShuhaiUtils.class.getClassLoader().getResourceAsStream("com/ningpai/web/config/config.properties"));
        } catch (Exception e) {
//            e.printStackTrace();
        	return null;
        }
        return properties;
    }

	private static String md5(final String str) throws NoSuchAlgorithmException {
		return encode("MD5", str);
	}

	private static String encode(final String algorithm, final String str) throws NoSuchAlgorithmException {
		MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
		final byte[] buff = messageDigest.digest(str.getBytes());
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < buff.length; ++i) {
			final String hexStr = Integer.toHexString(0xFF & buff[i]);
			if (hexStr.length() == 1) {
				sb.append("0").append(hexStr);
			} else {
				sb.append(hexStr);
			}
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		
		Date date = new Date();
		long time = date.getTime();
		int round = Math.round(time);
		
		long currentTimeMillis = System.currentTimeMillis();
		
		System.out.println(time);
		System.out.println(round);
		System.out.println(currentTimeMillis);
		
//		try {
//			String md5 = CodecUtils.md5("apiKey=SHSC201912045714&method=platformApi/v1/order/sale/receive&secret=63640470009856000&timestamp=1578563797727");
//			System.out.println(md5);
//		} catch (NoSuchAlgorithmException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
//		Date date1 = new Date("Wed Sep 25 00:00:00 CST 2019");
//		Date date2;
//		try {
//			date2 = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US).parse("Wed Sep 25 00:00:00 CST 2019");
//			System.out.println(date1);
//			System.out.println(date2);
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		
		// 生成订单
//		Order order = new Order();
//		order.setCustomerOrderId("90123456789123457");
//		List<Shop> shops = new ArrayList<>();
//		Shop shop = new Shop();
//		shop.setShopCode("0W50000005");
//		shop.setDeliveryDate("2019-12-06");
//		Product product = new Product();
//		product.setProductId("10000001");
//		product.setQuantity(new BigDecimal("1"));
//		product.setSpec("测试规格");
//		List<Product> products = new ArrayList<>();
//		products.add(product);
//		shop.setProducts(products);
//		shops.add(shop);
//		order.setShops(shops);
//		createShopOrder(order);
		
		// 查看订单详情
//		SHOrder orderDetail = getOrderDetail("90123456789123457");
//		System.out.println(orderDetail);
		
		// 查询订单
//		List<Order> queryOrder = queryOrders("2019-12-05", "2019-12-06");
//		System.out.println(queryOrder);
		
		// 获取订单的实发数量
//		Order sendQuantity = getSendQuantity("90123456789123456");
//		System.out.println(sendQuantity);
//		SHOrder sendQuantity2 = getSendQuantity("90123456789123457");
//		System.out.println(sendQuantity2);
		
		// 查看退款单
//		Order refundOrder = getRefundOrder("90123456789123456");
//		System.out.println(refundOrder);
		
		// 查看时间段内的退款单
//		List<Order> refundOrders = getRefundOrders("2019-12-05", "2019-12-06");
//		System.out.println(refundOrders);
		
		// 同步实收信息
//		boolean syncActualReceive = syncActualReceive("90123456789123457", "0W50000005", "10000001", new BigDecimal("1.10"));
//		System.out.println(syncActualReceive);
	}
	
}
