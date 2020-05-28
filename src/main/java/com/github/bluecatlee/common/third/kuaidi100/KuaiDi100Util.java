package com.github.bluecatlee.common.third.kuaidi100;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class KuaiDi100Util {
    /**
     * 实时查询请求地址
     */
    private static final String SYNQUERY_URL = "http://poll.kuaidi100.com/poll/query.do";

    private static final String TAG = "KuaiDi100 >>> ";

    private static String key;            //授权key
    private static String customer;        //实时查询公司编号

    @Value("${kuaidi100.key:}")
    public  void setKey(String key) {
        KuaiDi100Util.key = key;
    }

    @Value("${kuaidi100.customer:}")
    public  void setCustomer(String customer) {
        KuaiDi100Util.customer = customer;
    }

    public static String query(String com, String num, String phone) {
        return query(key, customer, com, num, phone, "", "", 0);
    }

    public static String query(String com, String num, String phone, String from, String to, int resultv2) {
        return query(key, customer, com, num, phone, from, to, resultv2);
    }

    /**
     * 实时查询快递单号
     *
     * @param key      授权key
     * @param customer 公司编号
     * @param com      快递公司编码 (一律小写字母)
     * @param num      快递单号 (单号的最大长度是32个字符)
     * @param phone    手机号 可选 收件人或寄件人的手机号或固话（顺丰单号必填，也可以填写后四位，如果是固话，请不要上传分机号）
     * @param from     出发地城市 可选
     * @param to       目的地城市 可选
     * @param resultv2 开通区域解析功能：0-关闭；1-开通
     * @return
     */
    public static String query(String key, String customer, String com, String num, String phone, String from, String to, int resultv2) {

        StringBuilder param = new StringBuilder("{");
        param.append("\"com\":\"").append(com).append("\"");
        param.append(",\"num\":\"").append(num).append("\"");
        param.append(",\"phone\":\"").append(phone).append("\"");
        param.append(",\"from\":\"").append(from).append("\"");
        param.append(",\"to\":\"").append(to).append("\"");
        if (1 == resultv2) {
            param.append(",\"resultv2\":1");
        } else {
            param.append(",\"resultv2\":0");
        }
        param.append("}");

        Map<String, String> params = new HashMap<>();
        params.put("customer", customer);
        String sign = null;
        try {
            sign = md5(param + key + customer);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        params.put("sign", sign);
        params.put("param", param.toString());

        return post(params);
    }

    /**
     * 发送post请求
     */
    private static String post(Map<String, String> params) {
        StringBuffer response = new StringBuffer("");

        BufferedReader reader = null;
        try {
            StringBuilder builder = new StringBuilder();
            for (Map.Entry<String, String> param : params.entrySet()) {
                if (builder.length() > 0) {
                    builder.append('&');
                }
                builder.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                builder.append('=');
                builder.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }
            byte[] bytes = builder.toString().getBytes("UTF-8");

            URL url = new URL(SYNQUERY_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(bytes.length));
            conn.setDoOutput(true);
            conn.getOutputStream().write(bytes);

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != reader) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return response.toString();
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
        String result = query("VIsyGEAY2106", "1EBD1284983FB23AE846D5F7A252653E", "shunfeng", "356081768702", "", "", "", 0);
        System.out.println(result);
    }

}
