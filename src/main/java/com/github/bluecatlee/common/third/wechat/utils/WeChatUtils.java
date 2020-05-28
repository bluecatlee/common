package com.github.bluecatlee.common.third.wechat.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.github.bluecatlee.common.third.wechat.annotation.WeChatBeanFieldAlias;
import com.github.bluecatlee.common.third.wechat.exception.WeChatException;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;

/**
 * WeChat 工具类
 */
public class WeChatUtils {

    private static final Logger logger = LoggerFactory.getLogger(WeChatUtils.class);
    private static final String TAG = "WeChat >>";

    /**
     * md5 签名
     */
    public static String signWithMd5(Object bean, final String appKey) {
        Map<String, Object> params = getSignMapFromObject(bean);
        Set<String> keysSet = params.keySet();
        Object[] keys = keysSet.toArray();
        Arrays.sort(keys);

        StringBuilder sb = new StringBuilder();
        for (Object key : keys) {
            String value = String.valueOf(params.get(key));
            if (!value.equals("null")) {
                sb.append(key).append("=").append(value).append("&");
            }
        }

        if (appKey != null) {
            sb.append("key=").append(appKey);
        }
        try {
            final String sign = WeChatUtils.md5(sb.toString()).toUpperCase();
            logger.debug("{} Sign With Md5 Params = {} Sign = {}", TAG, sb.toString(), sign);
            return sign;
        } catch (NoSuchAlgorithmException e) {
            logger.error("{} Sign With Md5 {}", TAG, e);
            throw new WeChatException(e);
        }
    }

    /**
     * sha1签名
     */
    public static String signWithSha1(Object bean) {
        Map<String, Object> params = getSignMapFromObject(bean);
        Set<String> keysSet = params.keySet();
        Object[] keys = keysSet.toArray();
        Arrays.sort(keys);

        StringBuilder sb = new StringBuilder();
        for (Object key : keys) {
            String value = String.valueOf(params.get(key));
            if (!value.equals("null")) {
                sb.append(key).append("=").append(value).append("&");
            }
        }

        sb.deleteCharAt(sb.length() - 1);

        try {
            final String sign = WeChatUtils.sha1(sb.toString());
            logger.debug("{} Sign With Sha1 Params = {} Sign = {}", TAG, sb.toString(), sign);
            return sign;
        } catch (NoSuchAlgorithmException e) {
            logger.error("{} Sign With Sha1 {}", TAG, e);
            throw new WeChatException(e);
        }
    }

    /**
     * 获取签名字段
     */
    public static Map<String, Object> getSignMapFromObject(Object bean) {
        Map<String, Object> params = new HashMap<>();
        try {
            Field[] fields = bean.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                WeChatBeanFieldAlias alias = field.getAnnotation(WeChatBeanFieldAlias.class);
                if (alias != null) {
                    Object value = field.get(bean);
                    if (alias.required() && value == null) {
                        throw new NullPointerException(field.getName());
                    }
                    if (value == null) {
                        continue;
                    }
                    params.put(alias.value(), value);
                }
            }
        } catch (Exception e) {
            logger.error("{} Get Sign Map From Object Key {}", TAG, e);
            throw new WeChatException(e);
        }
        return params;
    }

    /**
     * 发送post请求
     */
    public static String post(final String url, final boolean isSSL, File file, final String password, byte[] bytes) {

        OkHttpClient client = newHttpClient(isSSL, file, password);
        Request request = new Request.Builder().url(url).post(RequestBody.create(MediaType.parse("text/plain"), bytes))
                .build();
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
            logger.error("{} Post Request {}", TAG, e);
            throw new WeChatException(e);
        } finally {
            logger.debug("{} Post Request Url = {} Params = {} Response = {}", TAG, url, new String(bytes), body);
            if (response != null) {
                response.close();
            }
        }
    }

    /**
     * 发送post请求
     */
    public static String post(final String url, final byte[] bytes) {
        return post(url, false, null, null, bytes);
    }

    /**
     * 发送post请求
     */
    public static byte[] post2(final String url, final byte[] bytes) {
        OkHttpClient client = newHttpClient(false, null, null);
        Request request = new Request.Builder().url(url).post(RequestBody.create(MediaType.parse("text/plain"), bytes))
                .build();
        Response response = null;
        byte[] body = null;
        try {
            response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected Code " + response);
            }
            body = response.body().bytes();
            return body;
        } catch (IOException e) {
            logger.error("{} Post Request {}", TAG, e);
            throw new WeChatException(e);
        } finally {
            logger.debug("{} Post Request Url = {} Params = {} Response = {}", TAG, url, new String(bytes), body);
            if (response != null) {
                response.close();
            }
        }
    }

    /**
     * 发送get请求
     */
    public static String get(final String url) {
        return get(url, "utf-8");
    }

    public static String get(final String url, String charsetName) {
        String body = "";
        try {
            body = new String(getBytes(url), charsetName);
            return body;
        } catch (UnsupportedEncodingException e) {
            throw new WeChatException(e);
        } finally {
            logger.debug("{} Get String Request Url = {} Response = {}", TAG, url, body);
        }
    }

    public static byte[] getBytes(final String url) {
        OkHttpClient client = newHttpClient(false, null, null);
        Request request = new Request.Builder().url(url).build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected Code " + response);
            }
            return response.body().bytes();
        } catch (IOException e) {
            logger.error("{} Get Request {}", TAG, e);
            throw new WeChatException(e);
        } finally {
            logger.debug("{} Get Bytes Request Url = {}", TAG, url);
            if (response != null) {
                response.close();
            }
        }
    }

    public static OkHttpClient newHttpClient(final boolean isSSL, File file, final String password) {
        try {
            final OkHttpClient.Builder builder = new OkHttpClient.Builder();

            if (isSSL) {

                KeyStore keyStore = KeyStore.getInstance("PKCS12");
                FileInputStream instream = new FileInputStream(file);
                keyStore.load(instream, password.toCharArray());
                instream.close();

                KeyManagerFactory keyManagerFactory = KeyManagerFactory
                        .getInstance(KeyManagerFactory.getDefaultAlgorithm());
                keyManagerFactory.init(keyStore, password.toCharArray());

                TrustManagerFactory trustManagerFactory = TrustManagerFactory
                        .getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init((KeyStore) null);
                TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
                if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                    final String trustManagersStr = Arrays.toString(trustManagers);
                    throw new IllegalStateException("Unexpected default Trust Managers:" + trustManagersStr);
                }
                X509TrustManager trustManager = (X509TrustManager) trustManagers[0];

                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(keyManagerFactory.getKeyManagers(), new TrustManager[]{trustManager},
                        new SecureRandom());

                return builder.sslSocketFactory(sslContext.getSocketFactory(), trustManager).build();
            } else {
                return builder.build();
            }

        } catch (Exception e) {
            logger.error("{} New HttpClient {}", TAG, e);
            throw new WeChatException(e);
        }

    }

    /**
     * 随机数
     */
    public static String createNonceStr() {
        String s = UUID.randomUUID().toString();
        return s.replace("-", "");
    }

    /**
     * 时间戳
     */
    public static String createTimestamp() {
        return "" + System.currentTimeMillis() / 1000;
    }

    public static <T> T getBeanFromJson(final String content, final Class<T> valueType) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            return objectMapper.readValue(content, valueType);
        } catch (Exception e) {
            logger.error("{} Get Bean From Json {}", TAG, e);
            throw new WeChatException(e);
        }
    }

    public static <T> T getBeanFromXml(final String content, final Class<T> valueType) {
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            return xmlMapper.readValue(content, valueType);
        } catch (Exception e) {
            logger.error("{} Get Bean From Xml {}", TAG, e);
            throw new WeChatException(e);
        }
    }

    public static String beanToJsonString(Object object) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.error("{} Get Bean From Json {}", TAG, e);
            throw new WeChatException(e);
        }
    }

    public static String beanToJsonString2(Object object) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonGenerator.Feature.QUOTE_NON_NUMERIC_NUMBERS, true);
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.error("{} Get Bean From Json {}", TAG, e);
            throw new WeChatException(e);
        }
    }

    public static String encode(final String algorithm, final String str) throws NoSuchAlgorithmException {
        if (str == null) {
            return null;
        }
        MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
        byte[] buff = messageDigest.digest(str.getBytes());
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

    public static String md5(final String str) throws NoSuchAlgorithmException {
        return encode("MD5", str);
    }

    public static String sha1(final String str) throws NoSuchAlgorithmException {
        return encode("SHA1", str);
    }

    public static String base64Decode(final String str) {
        return new String(Base64.getDecoder().decode(str.getBytes()));
    }

    public static String base64Encode(final String str) {
        return Base64.getEncoder().encodeToString(str.getBytes());
    }

    /**
     * 处理参数长度
     *          微信body参数长度限制为128个字节
     * @param str
     * @return
     */
    public static String handleLength(String str) {
        int length = str.getBytes().length;
        byte[] src = str.getBytes();
        if (length > 128 ) {
            // 截取125位
            byte[] dest = new byte[125];
            System.arraycopy(src, 0, dest, 0, 125);
            String destStr = new String(dest);
            // 替换最后三个字符为...
            String midStr = destStr.substring(0, destStr.length() - 3);
            // 替换最后的字符为省略号
            String finalStr = midStr + "...";
            return finalStr;
        } else {
            return str;
        }
    }

}