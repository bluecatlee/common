package com.github.bluecatlee.common.http.httpclient;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;

/**
 * apache-httpclient-http工具类
 * 		todo 建议重新封装 一者由spring容器管理连接管理器、客户端等，二者需要手动处理资源的释放
 */
@Deprecated
public class HttpUtil {

	private static Logger log = LoggerFactory.getLogger(HttpUtil.class);

	public static final String POST_TYPE = "post";
	public static final String GET_TYPE = "get";

	private static MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();

	private static HttpClient client = new HttpClient(connectionManager);

	/**
	 * 提交http请求并且返回结果信息
	 * @param url
	 * @param paramMap
	 * @param requestType
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 */
	public static String sendHttpRequest(String url, Map<String, Object> paramMap, String requestType) throws HttpException, IOException {
		String res = null;
		HttpMethod method = null;

		if (POST_TYPE.toUpperCase().equals(requestType) || POST_TYPE.equals(requestType)) {
			method = new PostMethod(url);
		} else {
			method = new GetMethod(url);
		}
		method.addRequestHeader("Content-Type","application/x-www-form-urlencoded;charset=UTF-8");
		// 设置参数
		if (POST_TYPE.toUpperCase().equals(requestType) || POST_TYPE.equals(requestType)) {
			if ((paramMap != null) && (paramMap.size() > 0)) {
				Set keySet = paramMap.keySet();
				NameValuePair[] ps = new NameValuePair[keySet.size()];
				int count = 0;
				for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext();) {
					String key = iterator.next();
					ps[count] = new NameValuePair(key, paramMap.get(key).toString());
					count++;
				}
				((PostMethod) method).setRequestBody(ps);
			}
		} else {
			if ((paramMap != null) && (paramMap.size() > 0)) {
				Set keySet = paramMap.keySet();
				NameValuePair[] ps = new NameValuePair[keySet.size()];
				int count = 0;
				for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext();) {
					String key = iterator.next();
					ps[count] = new NameValuePair(key, paramMap.get(key).toString());
					count++;
				}
				method.setQueryString(ps);
			}
		}

		res = executeMethod(method);
		method.abort();
		method.releaseConnection();
		log.debug("返回报文：" + res);
		return res;
	}

	/**
	 * http请求
	 * @param url
	 * @param paramMap
	 * @param requestType
	 * @param timeout
	 * @return
	 */
	public static String sendHttpRequest(String url, Map<String, Object> paramMap, String requestType, String timeout) {
		String res = null;
		try {
			// 设置连接超时时间
			client.getHttpConnectionManager().getParams().setConnectionTimeout(Integer.valueOf(timeout));
			client.getHttpConnectionManager().getParams().setSoTimeout(3600000);
			res = sendHttpRequest(url, paramMap, requestType);

		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			return "httpFailed";
		}
		return res;
	}

	public static String executeMethod(HttpMethod method) throws IOException {
		method.setRequestHeader("Accept-Encoding", "gzip, deflate");
		int statusCode = client.executeMethod(method);
		// 服务器端的跳转处理
		if ((statusCode == HttpStatus.SC_MOVED_PERMANENTLY) || (statusCode == HttpStatus.SC_MOVED_TEMPORARILY)) {
			// 从 头中取出转向的地址
			Header locationHeader = method.getResponseHeader("location");
			String hostStr = method.getRequestHeader("Host").getValue();

			method.releaseConnection();
			String location = locationHeader.getValue();

			if (!location.contains("http")) {
				location = "http://" + hostStr + location;
			}
			method = new GetMethod(location);
			return executeMethod(method);
		} else {
			Header[] header = method.getResponseHeaders("Content-Encoding");
			String str = null;
			if ((header != null) && (header.length > 0)) {
				if ("gzip".equals(header[0].getValue())) {
					str = new String(uncompress(method.getResponseBody()));
				} else {
					str = new String(method.getResponseBody(), "UTF-8");
				}
			} else {
				str = new String(method.getResponseBody(), "UTF-8");
			}
			return str;
		}
	}

	private static String uncompress(byte[] bytes) throws IOException {
		if ((bytes == null) || (bytes.length == 0)) {
			return "";
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		GZIPInputStream gunzip = new GZIPInputStream(in);
		byte[] buffer = new byte[256];
		int n;
		while ((n = gunzip.read(buffer)) >= 0) {
			out.write(buffer, 0, n);
		}

		return out.toString("utf-8");

	}

	
	/**
     * 提交http请求并且返回结果信息 
     * @param url
     * @param paramMap
     * @param timeout
     * @param token
     * @return
     * @throws HttpException
     * @throws IOException
     */
    public static String sendHttpPostRequest(String url, JSONObject paramMap, Integer timeout, String token) throws HttpException, IOException {
     	// 设置连接超时时间
        client.getHttpConnectionManager().getParams() .setConnectionTimeout(timeout);
        client.getHttpConnectionManager().getParams().setSoTimeout(3600000);
        
        long start = System.currentTimeMillis();
        String res = null;
        PostMethod method = new PostMethod(url);
        method.addRequestHeader("Content-type", "application/json; charset=utf-8"); 
        if(token != null) {
            method.addRequestHeader("access_token", token); 
        }
        StringRequestEntity entity = new StringRequestEntity(paramMap.toString(),"application/json; charset=utf-8","UTF-8");
        method.setRequestEntity(entity);
        res = executeMethod(method);
        method.abort();
        method.releaseConnection();
        log.info("调用第三方用时："+(System.currentTimeMillis() - start)+"请求报文："+paramMap.toString()+" 返回报文："+ res);
        return res;
    }
    
    public static String sendHttpGetRequest(String url, JSONObject paramMap, Integer timeout, String token) throws IOException {
        // 设置连接超时时间
       client.getHttpConnectionManager().getParams() .setConnectionTimeout(timeout);
       client.getHttpConnectionManager().getParams().setSoTimeout(3600000);
       
       long start = System.currentTimeMillis();
       String res = null;
       GetMethod method = new GetMethod(url);
       if(token != null) {
           method.addRequestHeader("access_token", token); 
       }
       res = executeMethod(method);
       method.abort();
       method.releaseConnection();
       log.info("调用第三方用时："+(System.currentTimeMillis() - start)+"请求报文："+paramMap.toString()+" 返回报文："+ res);
       return res;
    }

    public static MultiThreadedHttpConnectionManager getMultiThreadedHttpConnectionManager() {
    	return connectionManager;
	}

}