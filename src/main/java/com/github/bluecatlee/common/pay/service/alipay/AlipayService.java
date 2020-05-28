package com.github.bluecatlee.common.pay.service.alipay;

import com.alibaba.fastjson.JSON;
import com.github.bluecatlee.common.pay.bean.*;
import com.github.bluecatlee.common.pay.exception.PayException;
import com.github.bluecatlee.common.pay.service.PayService;
import com.github.bluecatlee.common.pay.utils.RSA;
import com.github.bluecatlee.common.pay.utils.XmlUtil;
import com.github.bluecatlee.common.pay.utils.YGCodecUtils;
import com.github.bluecatlee.common.pay.utils.YGHttpClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

/**
 * 支付宝支付
 * 
 */
@Service("aliService")
public class AlipayService implements PayService {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(AlipayService.class);
	
	/**
     * 支付宝消息验证地址
     */
    private static final String HTTPS_VERIFY_URL = "https://mapi.alipay.com/gateway.do?service=notify_verify&";
    
    /**
     * 支付宝提供给商户的服务接入网关URL
     */
    private static final String ALIPAY_GATEWAY = "https://mapi.alipay.com/gateway.do?";

    private static final String _INPUT_CHARSET = "_input_charset=";
    
    /**
     * 支付新网关
     */
    private static final String ALIPAY_GATEWAY_NEW = "https://openapi.alipay.com/gateway.do";   

	public PayResult payOld(Pay pay) {
		try {
			// 把请求参数封装到map
	    	Map<String, String> sParaTemp = new HashMap<String, String>();
	    	sParaTemp.put("service", "create_direct_pay_by_user");
	    	sParaTemp.put("partner", pay.getPartner());
	    	sParaTemp.put("seller_email", pay.getExt().get("sellerEmail"));
	    	sParaTemp.put("_input_charset", pay.getExt().get("inputCharset"));
	    	sParaTemp.put("payment_type", pay.getType());
	    	sParaTemp.put("notify_url", pay.getNotifyUrl());
	    	sParaTemp.put("return_url", pay.getPageUrl());
	    	sParaTemp.put("out_trade_no", pay.getOutTradeNo());
	    	sParaTemp.put("trade_no", pay.getOutTradeNo());
	    	sParaTemp.put("subject", pay.getExt().get("subject"));
	    	sParaTemp.put("total_fee", pay.getAmount());
	    	sParaTemp.put("body", pay.getExt().get("body"));
	    	sParaTemp.put("show_url", pay.getExt().get("showUrl"));
	    	sParaTemp.put("anti_phishing_key", pay.getExt().get("antiPhishingKey"));
	    	sParaTemp.put("exter_invoke_ip", pay.getIp());
	    	sParaTemp.put("sign_type", "MD5"); 
			
	    	// 准备请求参数，并将sign_type和生成的签名sign也放到请求参数中
	        Map<String, String> sPara = buildRequestPara(sParaTemp, pay.getPartnerKey());
	
	        // 生成请求表单 GET请求
	        StringBuilder sbHtml = new StringBuilder();
	        sbHtml.append("<form id=\"alipaysubmit\" name=\"alipaysubmit\" action=\"" + ALIPAY_GATEWAY + _INPUT_CHARSET + pay.getExt().get("inputCharset") + "\" method=\"" + "get"
	                + "\">");
	
	        List<String> keys = new ArrayList<String>(sPara.keySet());
	        for (int i = 0; i < keys.size(); i++) {
	            String name = (String) keys.get(i);
	            String value = (String) sPara.get(name);
	            sbHtml.append("<input type=\"hidden\" name=\"" + name + "\" value=\"" + value + "\"/>");
	        }
	
	        // submit按钮控件请不要含有name属性
	        sbHtml.append("<input type=\"submit\" value=\"" + "确认" + "\" style=\"display:none;\"></form>");
	        sbHtml.append("<script>document.forms['alipaysubmit'].submit();</script>");
	        String aliPayResult = sbHtml.toString();
	        LOGGER.debug("AliPay pay form: " + aliPayResult);
	    	
	    	// 返回表单字符串
	    	PayResult payResult = new PayResult();
	    	payResult.setAliPayResult(aliPayResult);
			return payResult;
			
		} catch(Exception e) {
			throw new PayException("AliPay pay error");
		}
	}
	
	@Override
	public PayResult pay(Pay pay) {
		try {
			// 把请求参数封装到map
	    	Map<String, String> sParaTemp = new HashMap<String, String>();
	    	// 公共参数
	    	sParaTemp.put("app_id", pay.getExt().get("appId"));
	    	sParaTemp.put("return_url", pay.getPageUrl());
	    	sParaTemp.put("charset", "utf-8");
	    	sParaTemp.put("sign_type", "RSA2");          		  // 暂只支持RSA和RSA2  
	    	sParaTemp.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
	    	sParaTemp.put("version", "1.0");
	    	sParaTemp.put("notify_url", pay.getNotifyUrl());
	    	
	    	// 必传的业务请求参数
	    	Map<String,String> bizMap = new HashMap<>();  
	    	bizMap.put("out_trade_no", pay.getOutTradeNo());      //商户订单号
	    	bizMap.put("total_amount", pay.getAmount());          //订单金额
	    	bizMap.put("subject", pay.getExt().get("subject"));   //订单标题
	    	bizMap.put("body", pay.getProductName());             //订单描述(商品名)
	    	//bizMap.put("goods_detail", "{\"show_url\":\"" + pay.getExt().get("goodsDetail") + "\"}");  //商品展示地址
	    	
	    	if ("pc".equals(pay.getType())) {
	    		sParaTemp.put("method", "com.github.bluecatlee.common.pay.service.alipay.trade.page.pay");
	    		bizMap.put("product_code", "FAST_INSTANT_TRADE_PAY"); //产品销售码
			} else if ("h5".equals(pay.getType())) {
				sParaTemp.put("method", "com.github.bluecatlee.common.pay.service.alipay.trade.wap.pay");
				bizMap.put("product_code", "QUICK_WAP_WAY"); 		  //产品销售码
			}
	    	
	    	String bizJson = JSON.toJSONString(bizMap);
	    	LOGGER.debug("AliPay pay bizParam: " + bizJson);
	    	
	    	// 将业务参数放到biz_content中传递
	    	sParaTemp.put("biz_content", bizJson);
	    	// 获取私钥
	    	String privateKey = pay.getExt().get("privateKey");   
	    	// 准备签名参数
			String signParam = getSignParam(sParaTemp);
			// 生成签名
			String sign = RSA.rsa256Sign(signParam, privateKey, pay.getExt().get("charset"));
			LOGGER.debug("AliPay pay sign: " + sign);
			
			sParaTemp.put("sign", sign);
			
			// 拼接请求参数
			String param = getUrl(sParaTemp);
			
			String url = ALIPAY_GATEWAY_NEW + "?" + param;
			LOGGER.debug("AliPay pay url: " + url);
			
	    	// 返回url
	    	PayResult payResult = new PayResult();
	    	payResult.setAliPayResult(url);
			return payResult;
			
		} catch (Exception e) {
			throw new PayException("AliPay pay error: " + e);
		}
	}
	
	@Override
	public Map<String, String> query(Query query) {
		try {
			// 准备查询请求参数
			Map<String,String> map = new HashMap<>();
			// 公共参数
			map.put("app_id", query.getAppid());
			if ("pay".equals(query.getQueryType())) {
				map.put("method", "com.github.bluecatlee.common.pay.service.alipay.trade.query");
			}else if ("refund".equals(query.getQueryType())) {
				map.put("method", "com.github.bluecatlee.common.pay.service.alipay.trade.fastpay.refund.query");
			}
			map.put("charset", query.getExt().get("charset"));
			map.put("sign_type", "RSA2");
			map.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			map.put("version", "1.0");
			
			// 业务请求参数
			Map<String,String> bizMap = new HashMap<>();  
			bizMap.put("trade_no", query.getExt().get("tradeNo"));         //交易号 
			bizMap.put("out_trade_no", query.getExt().get("outTradeNo"));  //商户订单号，与交易号二选一(实际测试下来 app支付的订单只能使用交易号查询 订单号无法查询 优先以交易号)

			
			String bizJson = JSON.toJSONString(bizMap);
	    	LOGGER.debug("AliPay query bizParam: " + bizJson);
	    	
	    	// 将业务参数放到biz_content中传递
	    	map.put("biz_content", bizJson);    
			
	    	// 获取私钥
			String privateKey = query.getExt().get("privateKey");   
			
			// 准备签名参数
			String signParam = getSignParam(map);
	        
			// 生成签名
			String sign = RSA.rsa256Sign(signParam, privateKey, query.getExt().get("charset"));
			LOGGER.debug("AliPay query sign: " + sign);
			map.put("sign", sign);
			String param = getUrl(map);
			String url = ALIPAY_GATEWAY_NEW + "?" + param;
			LOGGER.debug("AliPay query url: " + url);
			
			// 发起查询请求
			String queryResult = YGHttpClient.post(url, null, String.class);
			LOGGER.debug("AliPay query result: " + queryResult);
			
			// 将查询结果解析成map返回
			Map resultMap = JSON.parseObject(queryResult, Map.class);
	        String str = String.valueOf(resultMap.get("alipay_trade_query_response"));
	        if (StringUtils.equals("null", str)) {
	        	str = String.valueOf(resultMap.get("alipay_trade_fastpay_refund_query_response"));
			}
	        Map resultMap2 = JSON.parseObject(str, Map.class);
			return resultMap2;
		} catch (Exception e) {
			throw new PayException("AliPay query error");
		}

	}

	public ValidateResult validateOld(Object object) {
		try {
			// 默认返回值
			final ValidateResult validateResult = new ValidateResult();
			validateResult.setValid(false);
			// 判断参数是否正确
			if (!(object instanceof Map)) {
				throw new PayException("AliPay validate param error");
			}
			// 获取签名值及解析数据
			Map<String, String> params = (Map<String, String>) object;
	        // responsetTxt的结果不是true，与服务器设置问题、合作身份者ID、notify_id一分钟失效有关
	        // sign不一致，与安全校验码、请求时的参数格式（如：带自定义参数等）、编码格式有关
	        String responseTxt = "true";
	        if (params.get("notify_id") != null) {
	            String notifyId = params.get("notify_id");
	            responseTxt = verifyResponse(notifyId, params.get("partner"));
	        }
	        String sign = "";
	        if (params.get("sign") != null) {
	            sign = params.get("sign");
	        }
	        String partnerKey = params.get("partnerKey");
	        //生成签名, partner和partnerKey不参与生成签名
	        params.remove("partner");
	        params.remove("partnerKey");
	        String genSign = getSignVerify(params, partnerKey);
	        if (sign.equals(genSign) && "true".equals(responseTxt)) {
	        	//校验成功
	            validateResult.setValid(true);
	        }
	        // 返回校验结果
	        return validateResult;
		} catch (Exception e) {
			throw new PayException("AliPay validate error");
		}
	}
	
	@Override
	public ValidateResult validate(Object object) {
		try {
			// 默认返回值
			final ValidateResult validateResult = new ValidateResult();
			validateResult.setValid(false);
			// 判断参数是否正确
			if (!(object instanceof Map)) {
				throw new PayException("AliPay validate param error");
			}
			// 获取签名值及解析数据
			Map<String, String> params = (Map<String, String>) object;
			
	        String sign = "";
	        if (params.get("sign") != null) {
	            sign = params.get("sign");
	        }
	        LOGGER.debug("AliPay pay notify sign: " + sign);
	        String publicKey = params.get("publicKey");
	        // sign sign_type publicKey不参与验签
	        params.remove("publicKey");
	        // 打印通知参数 
	        LOGGER.debug("AliPay notify param: " + params);
	        
	        boolean b = RSA.rsaCheckV1(params, publicKey, params.get("charset"), params.get("sign_type"));
	        if (b) {
	        	//校验成功
	            validateResult.setValid(true);
	            LOGGER.debug("AliPay notify verify sign success");
	        } else {
	        	LOGGER.debug("AliPay notify verify sign fail");
	        }
	        // 返回校验结果
	        return validateResult;
		} catch (Exception e) {
			throw new PayException("AliPay validate error");
		}
		
		
	}

	public String refundOld(Refund refund) {
		try {
			// 准备退款请求参数
	        Map<String, String> sParaTemp = new HashMap<String, String>();
	        // 退款基本参数参数
	        sParaTemp.put("service", "refund_fastpay_by_platform_nopwd");
	        sParaTemp.put("partner", refund.getPartner());
	        sParaTemp.put("_input_charset", refund.getExt().get("inputCharset"));
	        sParaTemp.put("notify_url", refund.getNotifyURL());
	        // 退款业务参数
	        sParaTemp.put("refund_date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
	        //每进行一次即时到账批量退款，都需要提供一个批次号，通过该批次号可以查询这一批次的退款交易记录，对于每一个合作伙伴，传递的每一个批次号都必须保证唯一性。
	        //格式为：退款日期（8位）+流水号（3～24位）。
	        //不可重复，且退款日期必须是当天日期。流水号可以接受数字或英文字符，建议使用数字，但不可接受“000”。
	        sParaTemp.put("batch_no", refund.getExt().get("batchNo"));
	        //即参数detail_data的值中，“#”字符出现的数量加1，最大支持1000笔（即“#”字符出现的最大数量为999个）。
	        sParaTemp.put("batch_num", "1");
	        //单笔数据集格式为：第一笔交易退款数据集#第二笔交易退款数据集#第三笔交易退款数据集…#第N笔交易退款数据集
	        //原付款支付宝交易号^退款总金额^退款理由
	        //交易退款数据集的格式为：原付款支付宝交易号^退款总金额^退款理由；
	        sParaTemp.put("detail_data", refund.getOriginalOrderNo() + "^" + refund.getRefundAmount() + "^" + refund.getExt().get("refundReason"));
	        
        	// 待请求参数数组，加入签名和签名方式
            Map<String, String> sPara = buildRequestPara(sParaTemp, refund.getPartnerKey());
            sPara.put("sign_type", "MD5"); 
            // 拼接请求参数
            StringBuilder sb = new StringBuilder();
            for (Entry<String, String> entry : sPara.entrySet()) {
            	sb.append("&").append(entry.getKey()).append("=").append( URLEncoder.encode(StringUtils.defaultString(entry.getValue()), "UTF-8"));
            }
            String param = sb.toString();
            String str = param.substring(1, param.length());
            LOGGER.debug("AliPay refund param: " + str);
            
            // 发起退款请求
            String refundResult = YGHttpClient.post(ALIPAY_GATEWAY + str, null, String.class);
            LOGGER.debug("AliPay refund result: " + refundResult);
            
            // 根据退款返回结果判断退款请求是否成功
            XmlUtil xmlUtil = new XmlUtil();
            Document document = xmlUtil.str2Document(refundResult);
            String is_success = document.getElementsByTagName("is_success").item(0).getTextContent();
            if ("T".equalsIgnoreCase(is_success)) {
                LOGGER.debug("发送退款请求给支付宝成功");
            } else {
                String error = document.getElementsByTagName("error").item(0).getTextContent();
                LOGGER.error("发送退款请求给支付宝失败," + "错误码" + error);
                throw new PayException("发送退款请求给支付宝失败" + "错误码" + error);
            }

            return refundResult;
            
        } catch (Exception e) {
            LOGGER.error("发送退款请求给支付宝时发生错误：" + e);
            e.printStackTrace();
            throw new PayException("发送退款请求给支付宝失败");
        }
	}
	
	@Override
	public String refund(Refund refund) {
		try {
			// 把请求参数封装到map
	    	Map<String, String> sParaTemp = new HashMap<String, String>();
	    	// 公共参数
	    	sParaTemp.put("app_id", refund.getExt().get("appId"));                   
	    	sParaTemp.put("method", "com.github.bluecatlee.common.pay.service.alipay.trade.refund");
	    	sParaTemp.put("charset", "utf-8");
	    	sParaTemp.put("sign_type", "RSA2");          // 1.0版本暂只支持RSA和RSA2  
	    	sParaTemp.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
	    	sParaTemp.put("version", "1.0");
	    	
	    	// 必传的业务请求参数
	    	Map<String,String> bizMap = new HashMap<>();  
	    	bizMap.put("trade_no", refund.getExt().get("tradeNo"));             //支付宝交易号，与out_trade_no二选一
	    	bizMap.put("refund_amount", refund.getRefundAmount());              //退款金额
	    	bizMap.put("out_request_no", refund.getExt().get("outRequestNo"));  //标识一次退款请求 部分退款必传 多次部分退款需要保证该值一致
			
	    	String bizJson = JSON.toJSONString(bizMap);
	    	LOGGER.debug("AliPay refund bizParam: " + bizJson);
	    	
	    	// 将业务参数放到biz_content中传递
	    	sParaTemp.put("biz_content", bizJson);
	    	
	    	// 获取私钥
	    	String privateKey = refund.getExt().get("privateKey");  
	    	// 准备签名参数
			String signParam = getSignParam(sParaTemp);
			// 生成签名
			String sign = RSA.rsa256Sign(signParam, privateKey, refund.getExt().get("charset"));
			LOGGER.debug("AliPay refund sign: " + sign);
			
			sParaTemp.put("sign", sign);
			
			// 拼接请求参数
			String param = getUrl(sParaTemp);
			
			String url = ALIPAY_GATEWAY_NEW + "?" + param;
			LOGGER.debug("AliPay refund url: " + url);
			
			// 发起退款请求
			String refundResult = YGHttpClient.post(url, null, String.class);
			/*String refundResult = "{\r\n" + 
					"    \"alipay_trade_refund_response\": {\r\n" + 
					"        \"code\": \"20000\",\r\n" + 
					"        \"msg\": \"Service Currently Unavailable\",\r\n" + 
					"        \"sub_code\": \"isp.unknow-error\",\r\n" + 
					"        \"sub_msg\": \"账户余额不足\"\r\n" + 
					"    },\r\n" + 
					"    \"sign\": \"ERITJKEIJKJHKKKKKKKHJEREEEEEEEEEEE\"\r\n" + 
					"}";*/
			LOGGER.debug("AliPay refund result: " + refundResult);
			
			return refundResult;
		} catch (Exception e) {
			throw new PayException("AliPay refund error: " + e);
		}
		
	}
	
	/**
     * 生成签名结果
     * 
     * @param sPara
     *            要签名的数组
     * @return 签名结果字符串
     */
    public static String buildRequestMysign(Map<String, String> sPara, String partnerKey) {
    	try {
	    	// 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
	        List<String> keys = new ArrayList<String>(sPara.keySet());
	        Collections.sort(keys);
	
	        String prestr = "";
	
	        for (int i = 0; i < keys.size(); i++) {
	            String key = keys.get(i);
	            String value = sPara.get(key);
	
	            if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
	                prestr = prestr + key + "=" + value;
	            } else {
	                prestr = prestr + key + "=" + value + "&";
	            }
	        }
	
	        LOGGER.debug("AliPay sign param: " + prestr + partnerKey);
	        //生成签名
	        String sign = YGCodecUtils.md5(prestr + partnerKey);
	        LOGGER.debug("AliPay sign result: " + sign);
	        
	        return sign;
    	} catch (Exception e) {
    		throw new PayException("AliPay genSign error");
    	}
    }
	
	/**
     * 生成要请求给支付宝的参数数组
     * 
     * @param sParaTemp
     *            请求前的参数数组
     * @return 要请求的参数数组
     */
    private static Map<String, String> buildRequestPara(Map<String, String> sParaTemp, String partnerKey) {
    	
        // 除去数组中的空值和签名参数
    	Map<String, String> sPara = new HashMap<String, String>();

        if (sParaTemp != null && !sParaTemp.isEmpty()) {
        	for (String key : sParaTemp.keySet()) {
        		String value = sParaTemp.get(key);
        		if (value == null || "".equals(value) || "sign".equalsIgnoreCase(key) || "sign_type".equalsIgnoreCase(key)) {
        			continue;
        		}
        		sPara.put(key, value);
        	}
        }
        
        // 生成签名结果
        String mysign = buildRequestMysign(sPara, partnerKey);

        // 签名结果与签名方式加入请求提交参数组中
        sPara.put("sign", mysign);

        return sPara;
    }
	
	/**
     * 根据反馈回来的信息，生成签名结果
     *
     * @param params
     *            通知返回来的参数数组
     * @param partnerKey
     * @return 生成的签名结果
     */
    private static String getSignVerify(Map<String, String> params, String partnerKey) {
    	 try {
	        // 过滤空值、sign与sign_type参数
	    	Map<String, String> sParaNew = new HashMap<String, String>();
	
	        if (params != null && !params.isEmpty()) {
	        	for (String key : params.keySet()) {
	        		String value = params.get(key);
	        		if (value == null || "".equals(value) || "sign".equalsIgnoreCase(key) || "sign_type".equalsIgnoreCase(key)) {
	        			continue;
	        		}
	        		sParaNew.put(key, value);
	        	}
	        }
	
	        // 获取待签名字符串
	        List<String> keys = new ArrayList<String>(sParaNew.keySet());
	        Collections.sort(keys);
	
	        String prestr = "";
	        for (int i = 0; i < keys.size(); i++) {
	            String key = keys.get(i);
	            String value = params.get(key);
	            if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
	                prestr = prestr + key + "=" + value;
	            } else {
	                prestr = prestr + key + "=" + value + "&";
	            }
	        }
	
	        // 获得签名验证结果
	        LOGGER.debug("Alipay original sign param: " + prestr);
			String sign = YGCodecUtils.md5(prestr + partnerKey);
			return sign;
		} catch (NoSuchAlgorithmException e) {
			throw new PayException("AliPay validate generate sign error: " + e);
		}
    }

    /**
     * 获取远程服务器ATN结果,验证返回URL 新版不用
     * @param notifyId
     *            通知校验ID
     * @return 服务器ATN结果 验证结果集： invalid命令参数不对 出现这个错误，请检测返回处理中partner和key是否为空 true
     *         返回正确信息 false 请检查防火墙或者是服务器阻止端口问题以及验证时间是否超过一分钟
     */
    private static String verifyResponse(String notifyId, String partner) {
    	
        // 获取远程服务器ATN结果，验证是否是支付宝服务器发来的请求
        String verifyUrl = HTTPS_VERIFY_URL + "partner=" + partner + "&notify_id=" + notifyId;

        // 获取远程服务器ATN结果
        String inputLine;
        try {
            URL url = new URL(verifyUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            inputLine = in.readLine().toString();
            in.close();
        } catch (Exception e) {
            LOGGER.error("",e);
            inputLine = "";
        }
        return inputLine;
    }
    
    /**
     * 生成拼接在url的参数
     * @param map
     * @return
     */
    private static String getUrl(Map<String, String> map) {
    	try {
    		
			StringBuilder sb = new StringBuilder();
			for(Entry<String, String> entry : map.entrySet()) {
				sb.append(entry.getKey()).append("=").append(URLEncoder.encode(StringUtils.defaultString(entry.getValue()), "UTF-8")).append("&");
			}
	    	return sb.toString().substring(0, sb.length() - 1);
    	} catch (Exception e) {
    		LOGGER.debug("");
    		throw new PayException("");
    	}
    }
    
    /**
     * @param map
     * @return
     */
    public static String getSignParam(Map<String, String> map) {
    	
    	try {
	    	Map<String, String> sPara = new HashMap<String, String>();
	        if (map != null && !map.isEmpty()) {
	        	for (String key : map.keySet()) {
	        		String value = map.get(key);
	        		if ("sign".equalsIgnoreCase(key)) {
	        			continue;
	        		}
	        		sPara.put(key, value);
	        	}
	        }
	    	
	        List<String> keys = new ArrayList<String>(sPara.keySet());
	        Collections.sort(keys);
	        String prestr = "";
	        for (int i = 0; i < keys.size(); i++) {
	            String key = keys.get(i);
	            String value = sPara.get(key);
	            if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
	                prestr = prestr + key + "=" + value;
	            } else {
	                prestr = prestr + key + "=" + value + "&";
	            }
	        }
	        LOGGER.debug("AliPay sign param: " + prestr);
	    	return prestr;
    	} catch (Exception e) {
    		throw new PayException("AliPay handle params error");
    	}
    	
    }
    
}
