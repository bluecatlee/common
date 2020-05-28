package com.github.bluecatlee.common.third.shuhai.utils;

import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * http客户端工具类
 * @author Bluecat lee
 *
 */
@SuppressWarnings("all")
public class ShuhaiHttpUtils {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ShuhaiHttpUtils.class);
	
	private static final String TAG = "ShuhaiHttpUtils >>> ";
	
	private static OkHttpClient newHttpClient() {
        final OkHttpClient.Builder builder = new OkHttpClient.Builder(); 
        // 定制client属性
        // builder.connectTimeout(120, TimeUnit.SECONDS)
        // 	.readTimeout(120, TimeUnit.SECONDS)
        //	.writeTimeout(120, TimeUnit.SECONDS);
        return builder.build();
    }
	
	public static String post(final String url, final String data, final String contentType) {
	    return post(url, data, contentType, null);
	}
	
	public static String post(final String url, final String data, final String contentType, Map<String, String> headerMap) {
	    OkHttpClient client = newHttpClient();
	    RequestBody requestBody = RequestBody.create(MediaType.parse(contentType), data.getBytes());
	    
	    Request request = null;
	    if (headerMap == null || headerMap.isEmpty()) {
	    	request = new Request.Builder().url(url).post(requestBody).build();
		} else {
			Headers headers = Headers.of(headerMap);
			request = new Request.Builder().url(url).headers(headers).post(requestBody).build();
		}
	    
	    Response response = null;
	    String body = null;
	    try {
	        response = client.newCall(request).execute();
	        if (!response.isSuccessful()) {
	            throw new IOException("Unexpected Code " + response);
	        }
	        body = response.body().string();
	        return body;
	    } catch (IOException e) {
	        LOGGER.error(TAG + "Post Request: ", e);
	        throw new RuntimeException(e);
	    } finally {
	        if (response != null) {
	            response.close();
	        }
	    }
	}
	
	public static String postFormData(final String url, final String data) {
		return post(url, data, "application/x-www-form-urlencoded");
	}

	public static String postJson(final String url, final String data) {
		return postJson(url, data, null);
	}
	
	public static String postJson(final String url, final String data, Map<String, String> headers) {
		return post(url, data, "application/json", headers);
	}
	
	public static String postMultipartFormData(final String url, final File file, final String contentType, Map<String, String> params) {
        OkHttpClient client = newHttpClient();
        okhttp3.RequestBody fileBody = okhttp3.RequestBody.create(MediaType.parse(contentType), file);
        MultipartBody.Builder builder = new MultipartBody.Builder().addFormDataPart("file", file.getName(), fileBody);
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.addFormDataPart(entry.getKey(), entry.getValue());
            }
        }
        RequestBody requestBody = builder.build();

        Request request = new Request.Builder().url(url).post(requestBody).build();

        Response response = null;
        String body = null;
        try {
            response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected Code " + response);
            }
            body = response.body().string();
            return body;
        } catch (IOException e) {
        	LOGGER.error(TAG + "Post Request: ", e);
            throw new RuntimeException(e);
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }
	
	public static String get(final String url) {
        String body = "";
        try {
            body = new String(getBytesData(url), "utf-8");
            return body;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
	
	public static byte[] getBytesData(final String url) {
        OkHttpClient client = newHttpClient();
        Request request = new Request.Builder().url(url).build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected Code " + response);
            }
            return response.body().bytes();
        } catch (IOException e) {
        	LOGGER.error(TAG + "Get Request: ", e);
            throw new RuntimeException(e);
        } finally {
            LOGGER.debug(TAG + "Get Bytes Request Url = " + url);
            if (response != null) {
                response.close();
            }
        }
    }
	
}
