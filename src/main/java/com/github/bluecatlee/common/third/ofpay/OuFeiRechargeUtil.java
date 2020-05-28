package com.github.bluecatlee.common.third.ofpay;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 欧飞充值工具类
 * @author Jiaobu
 *
 */
@SuppressWarnings("all")
public class OuFeiRechargeUtil {

	private static final Logger logger = LoggerFactory.getLogger(OuFeiRechargeUtil.class);

	private static final String telqueryUrl = "http://api2.ofpay.com/telquery.do";
	private static final String telcheckUrl = "http://api2.ofpay.com/telcheck.do";
	private static final String onlineorderUrl = "http://api2.ofpay.com/onlineorder.do";
	private static final String queryorderUrl = "http://api2.ofpay.com/api/query.do";
	private static final String newqueryuserinfoUrl = "http://api2.ofpay.com/newqueryuserinfo.do";
	private static final String reissueUrl = "http://api2.ofpay.com/reissue.do";
	private static final String flowCheckUrl = "http://api2.ofpay.com/flowCheck.do";
	private static final String flowOrderUrl = "http://api2.ofpay.com/flowOrder.do";
	private static final String mobinfoUrl = "http://api2.ofpay.com/mobinfo.do";
	private static final String querycardinfoUrl = "http://api2.ofpay.com/querycardinfo.do";

	private static final String mobileRechargeCallback = "http://living.bluecatlee.cc/oufeiRecharge/mobilecallback.htm";
	
	private static final String userid = "A923357";    //测试
	private static final String userpws = "4c625b7861a92c7971cd2029c2fd3c4a";  //测试
	private static final String key = "OFCARD";
	
	/**
	 * 用户信息查询接口(查询余额)
	 * @return
	 */
	public static Map<String, String> newqueryuserinfo() {
		Map<String,String> querys = new HashMap<>();
		querys.put("userid", userid);
		querys.put("userpws", userpws);
		querys.put("version", "6.0");
		// 准备参数
		StringBuilder sb = new StringBuilder();
		querys.forEach((k,v) -> {
			sb.append(k).append("=").append(v).append("&");
		});
		// get请求地址
		String url = newqueryuserinfoUrl + "?" + sb;
		// 发起请求
		String result = OufeiHttpUtils.get(url, String.class);
		try {
			if (result != null) {
				// 解析成map返回
				return ParseXML.strToXmlAndPaserXmlGB2312(result);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 根据手机号和面值查询商品信息
	 * @param phoneno
	 * @param pervalue
	 * @return
	 */
	public static Map<String, String> telQuery(String phoneno, String pervalue) {
		Map<String,String> querys = new HashMap<>();
		querys.put("userid", userid);      
		querys.put("userpws", userpws);    
		querys.put("phoneno", phoneno);    //充值手机号
		querys.put("pervalue", pervalue);  //充值面额               快充可选面值（1、2、5、10、20、30、50、100、300、500） 慢充可选面值（30、50、100）
		querys.put("mctype", null);
		querys.put("version", "6.0");
		// 准备参数
		StringBuilder sb = new StringBuilder();
		querys.forEach((k,v) -> {
			sb.append(k).append("=").append(v).append("&");
		});
		// get请求地址
		String url = telqueryUrl + "?" + sb;
		// 发起请求
		String result = OufeiHttpUtils.get(url, String.class);
		try {
			if (result != null) {
				// 解析成map返回
				return ParseXML.strToXmlAndPaserXmlGB2312(result);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 查询手机号当时是否可以充值
	 * @param phoneno
	 * @param price
	 * @return
	 */
	public static String telCheck(String phoneno, String price) {
		Map<String,String> querys = new HashMap<>();
		querys.put("userid", userid);      
		querys.put("phoneno", phoneno);      
		querys.put("price", price);     
		// 准备参数
		StringBuilder sb = new StringBuilder();
		querys.forEach((k,v) -> {
			sb.append(k).append("=").append(v).append("&");
		});
		// get请求地址
		String url = telcheckUrl + "?" + sb;
		// 发起请求
		String result = OufeiHttpUtils.get(url, String.class);
		return result;
	}
	
	/**
	 * 手机话费充值
	 * @param orderCode
	 * @param phonenum
	 * @param cardnum
	 * @return
	 */
	public static Map<String, String> onlineorder(String orderCode, String phonenum, String cardnum) {
		LinkedHashMap<String,String> querys = new LinkedHashMap<>();
		querys.put("userid", userid);      
		querys.put("userpws", userpws);      
		querys.put("cardid", "140101");                       //快充140101，慢充170101
        if(cardnum.contains(".") && !cardnum.equals("0.01")){
            cardnum = cardnum.substring(0,cardnum.indexOf("."));
            querys.put("cardnum", cardnum);                   //面值，快充可选面值（0.01、1、2、5、10、20、30、50、100、200、300、500） 慢充可选面值（30、50、100）
        } else {
            querys.put("cardnum", cardnum);
        }
		querys.put("sporder_id", orderCode);                  //商户订单号
		querys.put("sporder_time", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));         
															  //订单时间 （yyyyMMddHHmmss 如：20070323140214）
		querys.put("game_userid", phonenum);                  //手机号码
		
		String originalStr = querys.values().stream().collect(Collectors.joining());
		String md5Str = "";
		try {
			// 生成md5加密串
			md5Str = md5(originalStr + key).toUpperCase();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		querys.put("md5_str", md5Str);                        //签名串
		querys.put("mctype", null);                           //如果是慢充商品必须传如48 表示48小时到账
		querys.put("ret_url", mobileRechargeCallback);        //回调地址
		querys.put("version", "6.0");                         //固定值
		querys.put("buyNum", null);                           //1分钱商品使用，传入值为购买数量。cardnum*buynum
		
		// 准备参数
		StringBuilder sb = new StringBuilder();
		querys.forEach((k,v) -> {
			sb.append(k).append("=").append(v).append("&");
		});
		// get请求地址
		String url = onlineorderUrl + "?" + sb;
		String result = OufeiHttpUtils.get(url, String.class);
		try {
			if (result != null) {
				// 解析成map返回
				return ParseXML.strToXmlAndPaserXmlGB2312(result);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 根据SP订单号查询充值状态
	 * @param orderCode
	 * @return 充值状态
	 */
	public static String queryorder(String orderCode) {
		Map<String,String> querys = new HashMap<>();
		querys.put("userid", userid);
		querys.put("spbillid", orderCode);

		// 准备参数
		StringBuilder sb = new StringBuilder();
		querys.forEach((k,v) -> {
			sb.append(k).append("=").append(v).append("&");
		});
		// get请求地址
		String url = queryorderUrl + "?" + sb;
		String result = OufeiHttpUtils.get(url, String.class);
		return result;
	}

	/**
	 * 根据SP订单号补发订单充值状态
	 * @param orderCode
	 * @return
	 */
	public static Map<String, String> reissue(String orderCode) {
		Map<String,String> querys = new HashMap<>();
		querys.put("userid", userid);
		querys.put("userpws", userpws);
		querys.put("spbillid", orderCode);
		querys.put("version", "6.0");
		// 准备参数
		StringBuilder sb = new StringBuilder();
		querys.forEach((k,v) -> {
			sb.append(k).append("=").append(v).append("&");
		});
		// get请求地址
		String url = reissueUrl + "?" + sb;
		String result = OufeiHttpUtils.get(url, String.class);
		try {
			if (result != null) {
				// 解析成map返回
				return ParseXML.strToXmlAndPaserXmlGB2312(result);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 检验回调签名
	 * @param ret_code
	 * @param sporder_id
	 * @return
	 */
	public static boolean verifySign(String ret_code, String sporder_id, String sign) {
		logger.debug(">>>> OuFeiRecharge Callback Verify: 原始签名：" + sign);
		LinkedHashMap<String, String> map = new LinkedHashMap<>();
		map.put("userid", userid);
		map.put("ret_code", ret_code);
		map.put("sporder_id", sporder_id);

		String originalStr = map.values().stream().collect(Collectors.joining());
		String md5Str = "";
		try {
			// 生成md5加密串
			md5Str = md5(originalStr + key).toUpperCase();
			logger.debug(">>>> OuFeiRecharge Callback Verify: 自生成签名：" + sign);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		if (StringUtils.equals(sign, md5Str)) {
			logger.debug(">>>> OuFeiRecharge Callback Verify: 签名校验成功" );
			return true;
		} else {
			logger.debug(">>>> OuFeiRecharge Callback Verify: 签名校验失败" );
			return false;
		}
	}
	
	/**
	 * 流量商品信息查询
	 * @param phoneno
	 * @param perValue
	 * @param flowValue
	 * @param range
	 * @param effectStartTime
	 * @param effectTime
	 * @return
	 */
	public static Map<String, String> flowCheck(String phoneno,String perValue,String flowValue,String range,String effectStartTime,String effectTime) {
		Map<String,String> querys = new HashMap<>();
		querys.put("userid", userid);
		querys.put("userpws", userpws);
		querys.put("phoneno", phoneno);           	    //待充值手机号码
		querys.put("perValue", perValue);				//面值
		querys.put("flowValue", flowValue);				//流量值
		querys.put("range", range);					    //使用范围 1（省内）、 2（全国）
		querys.put("effectStartTime", effectStartTime); //生效时间 1（当日）、2（次日）、3（次月）
		querys.put("effectTime", effectTime);           //1-当月有效,2-30天有效,3-半年有效,4-3个月有效,5-2个月有效,6-6个月有效,7-20天有效,
													    //8-3日有效,9-90天有效,10-7天有效,11-当日有效,12-4小时有效,13-24小时有效,14-7个月有效,16-国庆8日有效
		//querys.put("netType", null);					//网络制式 2G、3G、4G(可不传，不传默认4G3G2G依次匹配)
		querys.put("version", "6.0");                   //固定值
		
		// 准备参数
		StringBuilder sb = new StringBuilder();
		querys.forEach((k,v) -> {
			sb.append(k).append("=").append(v).append("&");
		});
		// get请求地址
		String url = flowCheckUrl + "?" + sb;
		String result = OufeiHttpUtils.get(url, String.class);
		try {
			if (result != null) {
				// 解析成map返回
				return ParseXML.strToXmlAndPaserXmlGB2312(result);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	/**
	 * 流量直充接口
	 * @param phoneno
	 * @param perValue
	 * @param flowValue
	 * @param range
	 * @param effectStartTime
	 * @param effectTime
	 * @param orderCode
	 * @return
	 */
	public static Map<String,String> flowOrder(String phoneno,String perValue,String flowValue,String range,String effectStartTime,String effectTime,String orderCode) {
		LinkedHashMap<String,String> querys = new LinkedHashMap<>();
		querys.put("userid", userid);
		querys.put("userpws", userpws);
		querys.put("phoneno", phoneno);           	    //待充值手机号码
		querys.put("perValue", perValue);				//面值
		querys.put("flowValue", flowValue);				//流量值
		querys.put("range", range);					    //使用范围 1（省内）、 2（全国）
		querys.put("effectStartTime", effectStartTime); //生效时间 1（当日）、2（次日）、3（次月）
		querys.put("effectTime", effectTime);           //1-当月有效,2-30天有效,3-半年有效,4-3个月有效,5-2个月有效,6-6个月有效,7-20天有效,
													    //8-3日有效,9-90天有效,10-7天有效,11-当日有效,12-4小时有效,13-24小时有效,14-7个月有效,16-国庆8日有效
		//querys.put("netType", null);					//网络制式 2G、3G、4G(可不传，不传默认4G3G2G依次匹配)  {如果为空 不参与签名; 不为空则参与签名}
		querys.put("sporderId", orderCode);				//Sp商家的订单号（商户传给欧飞的唯一订单编号）
		
		String originalStr = querys.values().stream().collect(Collectors.joining());
		String md5Str = "";
		try {
			// 生成md5加密串
			md5Str = md5(originalStr + key).toUpperCase();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		querys.put("md5Str", md5Str);						//签名串
		querys.put("retUrl", mobileRechargeCallback);	    //订单有充值结果后回调的URL地址
		querys.put("version", "6.0");                   	//固定值
		
		// 准备参数
		StringBuilder sb = new StringBuilder();
		querys.forEach((k,v) -> {
			sb.append(k).append("=").append(v).append("&");
		});
		
		try {
			//byte[] b = sb.toString().getBytes("utf-8");
			//String result = OufeiHttpUtils.post(flowOrderUrl, b, String.class);
			// 发起请求
			String url = flowOrderUrl + "?" + sb;
			String result = OufeiHttpUtils.get(url, String.class);
			if (result != null) {
				// 解析成map返回
				return ParseXML.strToXmlAndPaserXmlGB2312(result);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	/**
	 * 手机号码归属地查询
	 * @param phoneno
	 * @return
	 */
	public static String[] mobinfo(String phoneno) {
		Map<String,String> querys = new HashMap<>();
		/*querys.put("userid", userid);
		querys.put("userpws", userpws);*/
		querys.put("mobilenum", phoneno);
		
		// 准备参数
		StringBuilder sb = new StringBuilder();
		querys.forEach((k,v) -> {
			sb.append(k).append("=").append(v).append("&");
		});
		// get请求地址
		String url = mobinfoUrl + "?" + sb;
		String result = OufeiHttpUtils.get(url, String.class);    //示例: 17721476640|上海上海|电信
		if (!result.contains("|")) {   
			return null;
		}
		String[] splitResult = result.split("[|]");
		return splitResult;
		
	}
	
	/**
	 * 具体商品信息同步接口(根据商品编号查询视频卡商品)
	 * @param cardid  欧飞商品编号
	 * @return
	 */
	public static Map<String,String> querycardinfo(String cardid) {
		Map<String,String> querys = new HashMap<>();
		querys.put("userid", userid);
		querys.put("userpws", userpws);
		querys.put("cardid", cardid);                //需查询商品的编码
		querys.put("version", "6.0");
		
		// 准备参数
		StringBuilder sb = new StringBuilder();
		querys.forEach((k,v) -> {
			sb.append(k).append("=").append(v).append("&");
		});
		// get请求地址
		String url = querycardinfoUrl + "?" + sb;
		String result = OufeiHttpUtils.get(url, String.class);
		logger.debug("查询欧飞视频充值产品返回： " + result );
		try {
			if (result != null && result.contains("<card>") && result.contains("</card>")) {
				int beginIndex = result.indexOf("<card>");
				int endIndex = result.indexOf("</card>") + "</card>".length();
				String substring = result.substring(beginIndex, endIndex);      // 获取里层的xml
				//System.out.println(substring);
				// 解析成map返回
				return ParseXML.strToXmlAndPaserXml(substring);
				
			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}
	
	/**
	 * 游戏及互娱产品直充接口(包括Q币充值、视频卡充值)  
	 * @param cardid
	 * @param orderCode
	 * @param account
	 * @return
	 */
	public static Map<String, String> videoorder(String cardid, String orderCode, String account) {
		LinkedHashMap<String,String> querys = new LinkedHashMap<>();
		querys.put("userid", userid);      
		querys.put("userpws", userpws);      
		querys.put("cardid", cardid); 					      //商品编号
		querys.put("cardnum", "1"); 						  //任意充传实际金额,固定面值传1
		querys.put("sporder_id", orderCode);                  //商家传给欧飞的唯一订单号
		querys.put("sporder_time", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));         
															  //订单时间 （yyyyMMddHHmmss 如：20070323140214）
		querys.put("game_userid", account);                   //待充值账号
		
		String originalStr = querys.values().stream().collect(Collectors.joining());
		String md5Str = "";
		try {
			// 生成md5加密串
			md5Str = md5(originalStr + key).toUpperCase();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		querys.put("md5_str", md5Str);                        //签名串
		querys.put("ret_url", mobileRechargeCallback);        //回调地址
		querys.put("version", "6.0");
		
		// 准备参数
		StringBuilder sb = new StringBuilder();
		querys.forEach((k,v) -> {
			sb.append(k).append("=").append(v).append("&");
		});
		// get请求地址
		String url = onlineorderUrl + "?" + sb;
		String result = OufeiHttpUtils.get(url, String.class);
		try {
			if (result != null) {
				// 解析成map返回
				return ParseXML.strToXmlAndPaserXmlGB2312(result);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
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
	
	public static void main(String[] args) throws NoSuchAlgorithmException {
		
		//Map<String, String> map = newqueryuserinfo();
		//System.out.println(map);
		
		//Map<String, String> flowCheck2 = flowCheck("17721476640","1","5M","1","1","1");
		//System.out.println(flowCheck2);
		
		//Map<String, String> flowOrder = flowOrder("17721476640","1","5M","1","1","1","188888888");
		//System.out.println(flowOrder);
		
		//String[] mobinfo = mobinfo("17721476640");
		//System.out.println(mobinfo[0]);
		
		//Map<String, String> querycardinfo = querycardinfo("1240710");
		//System.out.println(querycardinfo);
		
		Map<String, String> videoorder = videoorder("1241901","180606151559151515","961921213");
		System.out.println(videoorder);
	}
}
