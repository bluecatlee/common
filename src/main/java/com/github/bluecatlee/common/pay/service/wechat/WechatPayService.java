package com.github.bluecatlee.common.pay.service.wechat;

import com.github.bluecatlee.common.pay.bean.*;
import com.github.bluecatlee.common.pay.exception.PayException;
import com.github.bluecatlee.common.pay.service.PayService;
import com.github.bluecatlee.common.pay.utils.RandomStringGenerator;
import com.github.bluecatlee.common.pay.utils.YGCodecUtils;
import com.github.bluecatlee.common.pay.utils.YGHttpClient;
import com.github.bluecatlee.common.pay.service.wechat.bean.PayParam;
import com.github.bluecatlee.common.pay.service.wechat.bean.QueryPayParam;
import com.github.bluecatlee.common.pay.service.wechat.bean.QueryRefundParam;
import com.github.bluecatlee.common.pay.service.wechat.bean.RefundParam;
import com.github.bluecatlee.common.third.ofpay.ParseXML;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.lang.reflect.Field;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Service("wechatService")
public class WechatPayService implements PayService {

	private final static Logger logger = LoggerFactory.getLogger(WechatPayService.class);
	
	/**
	 * 被扫支付API
	 */
	public static String PAY_API = "https://api.mch.weixin.qq.com/pay/unifiedorder";

	/**
	 * 被扫支付查询API
	 */
	public static String PAY_QUERY_API = "https://api.mch.weixin.qq.com/pay/orderquery";

	/**
	 * 退款API
	 */
	public static String REFUND_API = "https://api.mch.weixin.qq.com/secapi/pay/refund";

	/**
	 * 退款查询API
	 */
	public static String REFUND_QUERY_API = "https://api.mch.weixin.qq.com/pay/refundquery";

	@Override
	public PayResult pay(Pay pay) {
		
		try {
			//封装请求参数
			PayParam payParam = new PayParam();
			payParam.setAppid(pay.getExt().get("appid"));
			payParam.setMchId(pay.getPartner());
			payParam.setNonceStr(RandomStringGenerator.getRandomStringByLength(32));
			payParam.setBody(pay.getExt().get("body"));
			payParam.setOutTradeNo(pay.getOutTradeNo());
			payParam.setTotalFee(pay.getAmount());
			payParam.setSpbillCreateIp(pay.getIp());
//			payParam.setSpbillCreateIp("101.81.237.167"); //本地测试时将ip修改为外网ip
			payParam.setNotifyUrl(pay.getNotifyUrl());
			payParam.setTradeType(pay.getType()); 
			if ("JSAPI".equals(pay.getType())) {
				payParam.setOpenid(pay.getExt().get("openid"));  //公众号支付时该参数必传
			}
			
	        // 生成签名
	        String sign = getSign(payParam, pay.getPartnerKey());
	        logger.debug("WechatPay pay sign: " + sign);
	        payParam.setSign(sign);
	        
	        // 将请求参数转成xml字符串
	        Map<String, Object> map = toMap(payParam);
	        String xmlStr = parseXML(map);
	        logger.debug("WechatPay pay param: " + xmlStr);
	        
			byte[] b = xmlStr.getBytes("utf-8");
			// 发起支付请求
			String reStr = YGHttpClient.post(WechatPayService.PAY_API, b, String.class);
			logger.debug("WechatPay pay result: " + reStr);
			
			PayResult payResult = new PayResult();
			payResult.setWechatPayResult(reStr);
			
			return payResult;
			
        } catch (Exception e) {
        	throw new PayException("WechatPay pay error", e);
        }
	}

	
	@Override
	public Map<String, String> query(Query query) {
		try {
			String queryType = query.getQueryType();
			// 判断查询类型
			// 查询支付
			if ("pay".equals(queryType)) {
				QueryPayParam queryPayParam = new QueryPayParam();
				queryPayParam.setAppid(query.getAppid());
				queryPayParam.setMchId(query.getMchId());
				queryPayParam.setNonceStr(RandomStringGenerator.getRandomStringByLength(32));
				queryPayParam.setSignType(query.getExt().get("signType"));
				queryPayParam.setTransactionId(query.getExt().get("transactionId"));   // 微信订单号与商户订单号 二选一 建议微信订单号
				queryPayParam.setOutTradeNo(query.getExt().get("outTradeNo"));
				
				// 生成签名
		        String sign = getSign(queryPayParam, query.getPartnerKey());
		        logger.debug("WechatPay queryPay sign: " + sign);
		        queryPayParam.setSign(sign);
		        
		        // 将请求参数转成xml字符串
		        Map<String, Object> map = toMap(queryPayParam);
		        String xmlStr = parseXML(map);
		        logger.debug("WechatPay queryPay param: " + xmlStr);
		        
				byte[] b = xmlStr.getBytes("utf-8");
				// 发起查询请求
				String reStr = YGHttpClient.post(WechatPayService.PAY_QUERY_API, b, String.class);
				logger.debug("WechatPay queryPay result: " + reStr);
	        
				// 处理查询结果
				Map<String, String> queryResult = ParseXML.strToXmlAndPaserXml(reStr);
				logger.debug("WechatPay queryPay mapResult: " + queryResult);
				return queryResult;
				
			// 查询退款
			} else if ("refund".equals(queryType)) {
				QueryRefundParam queryRefundParam = new QueryRefundParam();
				queryRefundParam.setAppid(query.getAppid());
				queryRefundParam.setMchId(query.getMchId());
				queryRefundParam.setNonceStr(RandomStringGenerator.getRandomStringByLength(32));
				queryRefundParam.setSignType(query.getExt().get("signType"));
				queryRefundParam.setTransactionId(query.getExt().get("transactionId"));
				queryRefundParam.setOutTradeNo(query.getExt().get("outTradeNo"));
				queryRefundParam.setRefundId(query.getExt().get("refundId"));
				queryRefundParam.setOutRefundNo(query.getExt().get("outRefundNo"));
				queryRefundParam.setOffset(query.getExt().get("offset"));
				
				// 生成签名
		        String sign = getSign(queryRefundParam, query.getPartnerKey());
		        logger.debug("WechatPay queryRefund sign: " + sign);
		        queryRefundParam.setSign(sign);
		        
		        // 将请求参数转成xml字符串
		        Map<String, Object> map = toMap(queryRefundParam);
		        String xmlStr = parseXML(map);
		        logger.debug("WechatPay queryRefund param: " + xmlStr);
		        
				byte[] b = xmlStr.getBytes("utf-8");
				// 发起查询请求
				String reStr = YGHttpClient.post(WechatPayService.REFUND_QUERY_API, b, String.class);
				logger.debug("WechatPay queryRefund result: " + reStr);
	        
				// 处理查询结果
				Map<String, String> queryResult = ParseXML.strToXmlAndPaserXml(reStr);
				logger.debug("WechatPay queryRefund mapResult: " + queryResult);
				return queryResult;
				
			}else {
				throw new PayException("No queryType specified.");
			}
		} catch (Exception e) {
			throw new PayException("WechatPay query error");
		}
		
	}

	@Override
	public ValidateResult validate(Object object) {
		// 默认返回值
		final ValidateResult validateResult = new ValidateResult();
		validateResult.setValid(false);
		// 判断参数是否正确
		if (!(object instanceof Map)) {
			throw new PayException("WechatPay validate param error");
		}
		
		Map<String, String> params = (Map<String, String>) object;
		// 获取签名值
        String sign = "";
        if (params.get("sign") != null) {
            sign = params.get("sign");
            logger.debug("WechatPay payNotify sign: " + sign);
        }
        // 密钥由用户传过来
        String key = params.get("partnerKey");
        if (key != null && StringUtils.isNotBlank(key)) {
        	// 调用校验签名方法,通知参数中的sign以及用户传的密钥不参与生成签名
        	params.remove("sign");
        	params.remove("partnerKey");
        	params.remove("prepay_id");
        	params.remove("return_msg");
        	String signResult = getSignOfMap(params, key);
        	if (sign.equals(signResult)) {
        		logger.debug("WechatPay payNotify sign verify success");
        		validateResult.setValid(true);
        	} else {
        		logger.debug("WechatPay payNotify sign verify fail");
        	}
		} else {
			logger.debug("WechatPay payNotify sign verify fail: lack of key");
		}
        // 返回校验结果
        return validateResult;
	}

	@Override
	public String refund(Refund refund) {
		
		try {
			//封装请求参数
	        RefundParam refundParam = new RefundParam();
	        refundParam.setAppid(refund.getExt().get("appid"));
	        refundParam.setMchId(refund.getExt().get("mchId"));
	        refundParam.setNonceStr(RandomStringGenerator.getRandomStringByLength(32));
	        refundParam.setTransactionId(refund.getExt().get("transactionId"));
	        refundParam.setOutTradeNo(refund.getOriginalOrderNo());
	        refundParam.setOutRefundNo(refund.getRefundOrderNo());
	        refundParam.setTotalFee(refund.getExt().get("totalFee"));
	        refundParam.setRefundFee(refund.getRefundAmount());
	        
	        // 生成签名
	        String sign = getSign(refundParam, refund.getPartnerKey());
	        logger.debug("WechatPay refund sign：" + sign);
	        refundParam.setSign(sign);
	        	        
	        // 将请求参数转成xml字符串
	        Map<String, Object> map = toMap(refundParam);
	        String xmlStr = parseXML(map);
	        logger.debug("WechatPay refund param: " + xmlStr);
	        
			byte[] b = xmlStr.getBytes("utf-8");
	        
	        File file = new File(refund.getExt().get("certLocalPath"));
	        String password = refund.getExt().get("certPassword");

	        // 发起退款请求
	        String refundResult = YGHttpClient.post(WechatPayService.REFUND_API, true, file, password, b, String.class);
	        //String refundResult = "<xml><return_code><![CDATA[FAIL]]></return_code> <return_msg><![CDATA[certificate not match]]></return_msg></xml>";
	        logger.debug("WechatPay refund result：" + refundResult);
	        
	        // 解析退款请求返回的结果，如果不为SUCCESS，则退款失败
	        //Map<String, String> refundResultMap = ParseXML.strToXmlAndPaserXml(refundResult);
	        //logger.debug("result_code==" + refundResultMap.get("result_code"));
	        /*if (!StringUtils.equals(refundResultMap.get("result_code"), "SUCCESS")) {
				throw new PayException("WechatPay refund request fail");
			}*/
	        return refundResult;
		} catch (Exception e) {
			throw new PayException("WechatPay refund error", e);
		} 
        
	}
	
	/**
	 * 将map请求参数转换成xml字符串
	 * @param paramsMap
	 * @return
	 */
	public String parseXML(Map<String, Object> paramsMap) {
        StringBuffer sb = new StringBuffer();
        sb.append("<xml>");
        Set es = paramsMap.entrySet();
        Iterator it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            String v = String.valueOf(entry.getValue());
            if (null != v && !"".equals(v)) {
                sb.append("<" + k + ">" + v + "</" + k + ">\n");
            }
        }
        sb.append("</xml>");
        return sb.toString();
    }
	
	/**
     * object对象类型参数生成签名
     * @param o 要参与签名的数据对象
     * @return 签名
     * @throws IllegalAccessException
     */
    public static String getSign(Object o, String key) throws IllegalAccessException {
    	try {
	        ArrayList<String> list = new ArrayList<String>();
	        Class cls = o.getClass();
	        Field[] fields = cls.getDeclaredFields();
	        for (Field f : fields) {
	            f.setAccessible(true);
	            if (f.get(o) != null && f.get(o) != "") {
	            	// 参数名处理成下划线的方式
	                list.add(underscoreName(f.getName()) + "=" + f.get(o) + "&");   
	            }
	        }
	        int size = list.size();
	        String [] arrayToSort = list.toArray(new String[size]);
	        Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
	        StringBuilder sb = new StringBuilder();
	        for(int i = 0; i < size; i ++) {
	            sb.append(arrayToSort[i]);
	        }
	        String result = sb.toString();
	        result += "key=" + key;
	        logger.debug("WechatPay sign Param: " + result);
			result = YGCodecUtils.md5(result).toUpperCase();
			logger.debug("WechatPay sign Result: " + result);
	        return result;
		} catch (NoSuchAlgorithmException e) {
			throw new PayException("WechatPay sign error", e);
		}
        
    }

    /**
	 * map类型参数生成签名
	 * @return
	 */
	public static String getSignOfMap(Map sPara, String partnerKey) {
		
		try {
			List<String> keys = new ArrayList<String>(sPara.keySet());
			Collections.sort(keys);

			String prestr = "";

			for (int i = 0; i < keys.size(); i++) {
			    String key = keys.get(i);
			    String value = (String) sPara.get(key);
			    prestr = prestr + key + "=" + value + "&";
			}
			// 生成签名
			String signTemp = prestr + "key=" + partnerKey;
			logger.debug("WechatPay sign Param: " + signTemp);
			String result = YGCodecUtils.md5(signTemp).toUpperCase();
			logger.debug("WechatPay sign Result: " + result);
			return result;
		} catch (Exception e) {
			throw new PayException("WecahtPay sign error", e);
		}
	}
	
    /**
	 * object对象转成map对象
	 * @return
	 */
	public static Map<String,Object> toMap(Object refundReqObj){
        Map<String,Object> map = new HashMap<String, Object>();
        Field[] fields = refundReqObj.getClass().getDeclaredFields();
        for (Field field : fields) {
        	field.setAccessible(true);
            Object obj;
            try {
                obj = field.get(refundReqObj);
                if(obj!=null){
                	// 参数名处理成下划线的方式
                    map.put(underscoreName(field.getName()), obj);
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }
	
    /**
     * 下划线转驼峰命名
     * @param camelCaseName
     * @return
     */
    public static String underscoreName(String camelCaseName) {  
        StringBuilder result = new StringBuilder();  
        if (camelCaseName != null && camelCaseName.length() > 0) {  
            result.append(camelCaseName.substring(0, 1).toLowerCase());  
            for (int i = 1; i < camelCaseName.length(); i++) {  
                char ch = camelCaseName.charAt(i);  
                if (Character.isUpperCase(ch)) {  
                    result.append("_");  
                    result.append(Character.toLowerCase(ch));  
                } else {  
                    result.append(ch);  
                }  
            }  
        }  
        return result.toString();  
    }  
	
}
