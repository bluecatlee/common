package com.github.bluecatlee.common.pay.service.shengfutong;

import com.github.bluecatlee.common.pay.bean.*;
import com.github.bluecatlee.common.pay.exception.PayException;
import com.github.bluecatlee.common.pay.service.PayService;
import com.github.bluecatlee.common.pay.service.shengfutong.bean.PayNotify;
import com.github.bluecatlee.common.pay.service.shengfutong.bean.PayParam;
import com.github.bluecatlee.common.pay.service.shengfutong.bean.QueryParam;
import com.github.bluecatlee.common.pay.service.shengfutong.bean.RefundNotify;
import com.github.bluecatlee.common.pay.service.shengfutong.bean.RefundParam;
import com.github.bluecatlee.common.pay.utils.YGCodecUtils;
import com.github.bluecatlee.common.pay.utils.YGHttpClient;
import com.github.bluecatlee.common.third.ofpay.ParseXML;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <ul>
 * <li>测试商户号：540505</li>
 * <li>MD5密钥：support4html5test</li>
 * <li>测试商户号（Optional）:143274</li>
 * <li>MD5密钥：abcdefg</li>
 * </ul>
 * 
 */
@Service("shengFuTongPayService")
public class ShengFuTongPayService implements PayService {

	private final Logger logger = LoggerFactory.getLogger(ShengFuTongPayService.class);

	/**
	 * 支付
	 */
	private static final String URL_PAY = "https://mas.shengpay.com/web-acquire-channel/cashier.htm";
	
	/**
	 * H5收单
	 */
	//private static final String URL_PAY_H5 = "https://cardpay.shengpay.com/mobile-acquire-channel/cashier.htm";

	/**
	 * 退款
	 */
	private static final String URL_REFUND = "https://mas.shengpay.com/api-acquire-channel/services/refundService?wsdl";

	/**
	 * 查询
	 */
	private static final String URL_QUERY = "https://mas.shengpay.com/api-acquire-channel/services/queryOrderService?wsdl";

	@Override
	public PayResult pay(Pay pay) {
		// 设置盛付通支付参数
		PayParam payParam = new PayParam();
		payParam.setOutMemberId(pay.getOutMemberId());
		payParam.setPageUrl(pay.getPageUrl());
		payParam.setNotifyUrl(pay.getNotifyUrl());
		payParam.setOrderNo(pay.getOutTradeNo());
		payParam.setOrderAmount(pay.getAmount());
		payParam.setOrderTime(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		payParam.setProductName(pay.getProductName());
		payParam.setBuyerIp(pay.getIp());
		payParam.setMsgSender(pay.getPartner());
		// 生成签名
		final String sign = getPayParam(payParam, pay.getPartnerKey());
		// 生成支付请求url
		final String url = URL_PAY + "?" + sign;
		logger.debug("Shengfutong pay url: " + url);
		
		PayResult payResult = new PayResult();
		payResult.setUrl(url);
		return payResult;
	}
	
	@Override
	public Map<String, String> query(Query query) {
		
		try {
			//封装请求参数
			QueryParam queryParam = new QueryParam();
			queryParam.setSenderId(query.getMchId());
			queryParam.setSendTime(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
			queryParam.setMerchantNo(query.getMchId());
			queryParam.setOrderNo(query.getExt().get("orderNo"));
			queryParam.setExt1(query.getExt().get("ext1"));
			
			Map<String, Object> map = toMap(queryParam);
			// 准备签名请求参数
			StringBuffer buf = new StringBuffer();
			buf.append(map.get("serviceCode")).append("|").append(map.get("version")).append("|")
				.append(map.get("charset")).append("|").append(map.get("senderId")).append("|")
				.append(map.get("sendTime")).append("|").append(map.get("merchantNo")).append("|")
				.append(map.get("orderNo")).append("|").append(map.get("transNo")).append("|")
				.append(map.get("ext1")).append("|").append(map.get("signType")).append("|");
			
			String origin = buf.toString().replace("null|", "");
			logger.debug("Shengfutong query sign original string: " + origin);
			
			//生成签名
			String signMsg = YGCodecUtils.md5(origin + query.getPartnerKey()).toUpperCase();
			logger.debug("Shengfutong query sign: " + signMsg);
			map.put("signMsg", signMsg);
			
			//拼接soap报文
			StringBuffer sb = new StringBuffer();
			sb.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:quer=\"http://www.sdo.com/mas/api/query\">\r\n" + 
					"   <soapenv:Header/>\r\n" + 
					"   <soapenv:Body>\r\n" + 
					"      <quer:queryOrder>\r\n" + 
					"         <arg0>\r\n" + 
					"            <extension>\r\n" + 
					"               <ext1>");
			sb.append(map.get("ext1"));
			sb.append("</ext1>\r\n" + 
					"               <ext2>");
			sb.append(map.get("ext2"));
			sb.append("</ext2>\r\n" + 
					"            </extension>\r\n" + 
					"            <header>\r\n" + 
					"               <charset>");
			sb.append(map.get("charset"));
			sb.append("</charset>\r\n" + 
					"               <sendTime>");
			sb.append(map.get("sendTime"));
			sb.append("</sendTime>\r\n" + 
					"               <sender>\r\n" + 
					"                  <senderId>");
			sb.append(map.get("senderId"));
			sb.append("</senderId>\r\n" + 
					"               </sender>\r\n" + 
					"               <com.github.bluecatlee.common.pay.service>\r\n" +
					"                  <serviceCode>");
			sb.append(map.get("serviceCode"));
			sb.append("</serviceCode>\r\n" + 
					"                  <version>");
			sb.append(map.get("version"));
			sb.append("</version>\r\n" + 
					"               </com.github.bluecatlee.common.pay.service>\r\n" +
					"               <traceNo>");
			sb.append(map.get("traceNo"));
			sb.append("</traceNo>\r\n" + 
					"            </header>\r\n" + 
					"            <merchantNo>");
			sb.append(map.get("merchantNo"));
			sb.append("</merchantNo>\r\n" + 
					"            <orderNo>");
			sb.append(map.get("orderNo"));
			sb.append("</orderNo>\r\n" + 
					"            <signature>\r\n" + 
					"               <signMsg>");
			sb.append(map.get("signMsg"));
			sb.append("</signMsg>\r\n" + 
					"               <signType>");
			sb.append(map.get("signType"));
			sb.append("</signType>\r\n" + 
					"            </signature>\r\n" + 
					"            <transNo>");
			sb.append(map.get("transNo"));
			sb.append("</transNo>\r\n" + 
					"         </arg0>\r\n" + 
					"      </quer:queryOrder>\r\n" + 
					"   </soapenv:Body>\r\n" + 
					"</soapenv:Envelope>");
			
			// 去掉soap请求报文中的null
			String soapRequestData = sb.toString().replace("null", "");
			logger.debug("Shengfutong query soapXmlParam: " + soapRequestData);
			
			byte[] b = null;
			b = soapRequestData.getBytes("utf-8");
			
			// 发送请求，并返回数据
			String response = YGHttpClient.post(ShengFuTongPayService.URL_QUERY, b, String.class);
			logger.debug("Shengfutong query result: " + response);
			
			//return ParseXML.strToXmlAndPaserXml(response);
			// 将soap转成标准xml格式
			String xml = response.replace("<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">", "").replace("<soap:Body>", "").replace("<ns2:queryOrderResponse xmlns:ns2=\"http://www.sdo.com/mas/api/query\">", "")
			.replace("</ns2:queryOrderResponse>", "").replace("</soap:Body>", "").replace("</soap:Envelope>", "");
			
			return ParseXML.strToXmlAndPaserXml("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + xml);

		} catch (Exception e) {
			throw new PayException("ShengFuTong query error", e);
		}
		
	}

	@SuppressWarnings("rawtypes")
	@Override
	public ValidateResult validate(Object object) {
		// 默认返回值
		final ValidateResult validateResult = new ValidateResult();
		validateResult.setValid(false);
		// 判断参数是否正确
		if (!(object instanceof Map)) {
			throw new PayException("ShengFuTong validate param error");
		}
		// 获取签名值及解析数据
		Map paramMap = (Map) object;
		String notify = (String) paramMap.get("notify");
		String partnerKey = (String) paramMap.get("partnerKey");
		String sign = "";
		
		// 支付成功回调签名验证
		if (notify == "pay") {
			PayNotify payNotify = new PayNotify();
			sign = getSign(payNotify, paramMap, partnerKey);
			validateResult.setErrorCode(payNotify.getErrorCode());
			validateResult.setErrorMessage(payNotify.getErrorMsg());
			validateResult.setOrderNo(payNotify.getOrderNo());
			validateResult.setTransNo(payNotify.getTransNo());
			validateResult.setStatus(payNotify.getTransStatus());
			validateResult.setAmount(payNotify.getOrderAmount());
			
		// 退款成功回调签名验证
		} else if (notify == "refund") {
			RefundNotify payNotify = new RefundNotify();
			sign = getSign(payNotify, paramMap, partnerKey);
			
		}
		String str = (String) paramMap.get("SignMsg");
		if (StringUtils.equals(sign, str)) {
			validateResult.setValid(true);
		}
		return validateResult;
	}

	@Override
	public String refund(Refund refund) {
		try {
			RefundParam refundParam = new RefundParam();
			refundParam.setSenderId(refund.getPartner());
			refundParam.setSendTime(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
			refundParam.setMerchantNo(refund.getPartner());
			refundParam.setRefundOrderNo(refund.getRefundOrderNo());
			refundParam.setOriginalOrderNo(refund.getOriginalOrderNo());
			refundParam.setRefundAmount(refund.getRefundAmount());
			refundParam.setRefundRoute("0");//退款到原始资金源
			refundParam.setNotifyURL(refund.getNotifyURL());
			refundParam.setSignType("MD5");
			
			Map<String, Object> map = toMap(refundParam);
			
			StringBuffer buf = new StringBuffer();
			buf.append(map.get("serviceCode")).append("|").append(map.get("version")).append("|")
				.append(map.get("charset")).append("|").append(map.get("traceNo")).append("|")
				.append(map.get("senderId")).append("|").append(map.get("sendTime")).append("|")
				.append(map.get("merchantNo")).append("|").append(map.get("refundOrderNo")).append("|")
				.append(map.get("originalOrderNo")).append("|").append(map.get("refundAmount")).append("|")
				.append(map.get("refundRoute")).append("|").append(map.get("notifyURL")).append("|")
				.append(map.get("memo")).append("|").append(map.get("ext1")).append("|")
				.append(map.get("signType")).append("|");
			
			String origin = buf.toString().replace("null|", "");
			logger.debug("Shengfutong refund sign original string: " + origin);
			
			//生成签名
			String signMsg = YGCodecUtils.md5(origin + refund.getPartnerKey()).toUpperCase();
			logger.debug("Shengfutong refund sign: " + signMsg);
			map.put("signMsg", signMsg);
			
			//拼接soap报文
			StringBuffer sb = new StringBuffer();
			sb.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ref=\"http://www.sdo.com/mas/api/refund/\">\r\n" + 
					"   <soapenv:Header/>\r\n" + 
					"   <soapenv:Body>\r\n" + 
					"      <ref:processRefund>\r\n" + 
					"         <arg0>\r\n" + 
					"            <extension>\r\n" + 
					"               <ext1>")
			.append(map.get("ext1"))
			.append("</ext1>\r\n" + 
					"               <ext2>")
			.append(map.get("ext2"))
			.append("</ext2>\r\n" + 
					"            </extension>\r\n" + 
					"            <header>\r\n" + 
					"               <charset>")
			.append(map.get("charset"))
			.append("</charset>\r\n" + 
					"               <sendTime>")
			.append(map.get("sendTime"))
			.append("</sendTime>\r\n" + 
					"               <sender>\r\n" + 
					"                  <senderId>")
			.append(map.get("senderId"))
			.append("</senderId>\r\n" + 
					"               </sender>\r\n" + 
					"               <com.github.bluecatlee.common.pay.service>\r\n" +
					"                  <serviceCode>")
			.append(map.get("serviceCode"))
			.append("</serviceCode>\r\n" + 
					"                  <version>")
			.append(map.get("version"))
			.append("</version>\r\n" + 
					"               </com.github.bluecatlee.common.pay.service>\r\n" +
					"               <traceNo>")
			.append(map.get("traceNo"))
			.append("</traceNo>\r\n" + 
					"            </header>\r\n" + 
					"            <memo>")
			.append(map.get("memo"))
			.append("</memo>\r\n" + 
					"            <merchantNo>")
			.append(map.get("merchantNo"))
			.append("</merchantNo>\r\n" + 
					"            <notifyURL>")
			.append(map.get("notifyURL"))
			.append("</notifyURL>\r\n" + 
					"            <originalOrderNo>")
			.append(map.get("originalOrderNo"))
			.append("</originalOrderNo>\r\n" + 
					"            <refundAmount>")
			.append(map.get("refundAmount"))
			.append("</refundAmount>\r\n" + 
					"            <refundOrderNo>")
			.append(map.get("refundOrderNo"))
			.append("</refundOrderNo>\r\n" + 
					"            <refundRoute>")
			.append(map.get("refundRoute"))
			.append("</refundRoute>\r\n" + 
					"            <refundType>")
			.append(map.get("refundType"))
			.append("</refundType>\r\n" + 
					"            <signature>\r\n" + 
					"               <signMsg>")
			.append(map.get("signMsg"))
			.append("</signMsg>\r\n" + 
					"               <signType>")
			.append(map.get("signType"))
			.append("</signType>\r\n" + 
					"            </signature>\r\n" + 
					"         </arg0>\r\n" + 
					"      </ref:processRefund>\r\n" + 
					"   </soapenv:Body>\r\n" + 
					"</soapenv:Envelope>");
			
			// 去掉请求报文中的null
			String soapRequestData = sb.toString().replace("null", "");
			logger.debug("Shengfutong refund soapXmlParam: " + soapRequestData);
			
			byte[] b = soapRequestData.getBytes("utf-8");
			
			//发送请求，并返回数据
			String response = YGHttpClient.post(ShengFuTongPayService.URL_REFUND, b, String.class);
			logger.debug("Shengfutong refund result: " + response);
			//Map<String, String> resultMap = ParseXML.parseToMap(response);
			//logger.debug("Shengfutong refund mapResult: " + resultMap);
			
			return response;
		
		} catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
			logger.debug("调用退款接口发生异常");
			throw new PayException("ShengFuTong refund error", e);
		}
		
	}

	/**
	 * 支付参数及签名
	 */
	private String getPayParam(final Object object, String key) {
		try {

			// FIXME: 反射获取数据，保存字段信息不用每次都使用反射
			Map<String, String> paramMap = new HashMap<>();
			Field[] fields = object.getClass().getDeclaredFields();
			String[] params = new String[fields.length];
			for (int i = 0; i < fields.length; ++i) {
				final Field field = fields[i];
				SignParam signParam = field.getAnnotation(SignParam.class);
				if (signParam != null) {
					field.setAccessible(true);
					String obj = (String) field.get(object);
					paramMap.put(signParam.name(), URLEncoder.encode(StringUtils.defaultString(obj), "UTF-8"));
					if (obj == null) {
						continue;
					}
					int idx = signParam.value();
					if (idx > 0) {
						params[idx - 1] = obj;
					}
				}
			}
			// 准备签名
			final String source = Arrays.stream(params).filter(param -> StringUtils.isNotBlank(param)).collect(Collectors.joining("|"));
			logger.debug("ShengFuTong request params: " + source);
			final String signStr = source + "|" + key;
			final String hexSigned = YGCodecUtils.md5(signStr).toUpperCase();
			// 组织参数
			StringBuilder sb = new StringBuilder();
			paramMap.forEach((k, v) -> {
				sb.append(k).append("=").append(v).append("&");
			});
			sb.append("SignMsg").append("=").append(hexSigned);
			return sb.toString();
		} catch (Exception e) {
			throw new PayException("ShengFuTong genPayUrl error", e);
		}
	}

	/**
	 * 签名
	 */
	@SuppressWarnings("rawtypes")
	private String getSign(Object object, Map map, String key) {
		// FIXME: 和获取支付参数有重复，如果缓存字段信息这边可以优化
		try {
			Field[] fields = object.getClass().getDeclaredFields();
			String[] params = new String[fields.length];
			for (int i = 0; i < fields.length; ++i) {
				final Field field = fields[i];
				SignParam signParam = field.getAnnotation(SignParam.class);
				if (signParam != null) {
					field.setAccessible(true);
					final String name = signParam.name();
					final int idx = signParam.value();
					String val = (String) map.get(name);
					field.set(object, val);
					if (idx > 0 && val != null) {
						params[idx - 1] = val;
					}
				}
			}
			final String source = Arrays.stream(params).filter(param -> StringUtils.isNotBlank(param)).collect(Collectors.joining("|"));
			final String signStr = source + "|" + key;
			logger.debug("ShengFuTong sign param: " + signStr);
			String sign = YGCodecUtils.md5(signStr).toUpperCase();
			logger.debug("ShengFuTong sign result: " + sign);
			return sign;
		} catch (Exception e) {
			throw new PayException("ShengFuTong sign error", e);
		}
	}
	
	/**
	 * object对象转成map对象
	 * @return
	 */
	public Map<String,Object> toMap(Object refundReqObj){
        Map<String,Object> map = new HashMap<String, Object>();
        Field[] fields = refundReqObj.getClass().getDeclaredFields();
        for (Field field : fields) {
        	field.setAccessible(true);
            Object obj;
            try {
                obj = field.get(refundReqObj);
                if(obj!=null){
                    map.put(field.getName(), obj);
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

}
