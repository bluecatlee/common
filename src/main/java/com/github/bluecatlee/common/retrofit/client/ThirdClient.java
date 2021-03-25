package com.github.bluecatlee.common.retrofit.client;

import com.alibaba.fastjson.JSONObject;
import com.github.bluecatlee.common.retrofit.annotation.BaseUrl;
import com.github.bluecatlee.common.retrofit.service.WeatherService;
import com.github.bluecatlee.common.test.bean.MessagePack;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 对retrofit2实现的接口调用进行二次封装
 *      todo 多个service创建实例(scan特定注解的类并创建对应的client实例)
 * @see retrofit2.http.Url  可以使用@Url实现动态url
 * @see <a href="https://github.com/bluecatlee/springboot-feign">类似框架：feign</>
 */
@Component
public class ThirdClient {

    @Value("${spring.profiles.active}")
    private String profile;

    @Bean
    public WeatherService generateApi() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(40, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .hostnameVerifier((hostname, session) -> true);

        if (!"prod".equals(profile)) {
            TrustManager[] trustAllCerts = buildTrustManagers();
            SSLContext sslContext = null;
            try {
                sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            } catch (KeyManagementException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
        }

        OkHttpClient client = builder.build();

        Retrofit retrofit = new Retrofit.Builder().baseUrl(getBaseUrl(WeatherService.class)).client(client).build();
        return retrofit.create(WeatherService.class);
    }

    @Deprecated
    public <T extends MessagePack> T executeRequest(Map<String, String> data, Class<T> clazz, Callable<Call<ResponseBody>> callable) {

        T res = null;
        try {
            Call<ResponseBody> call= callable.call();
            Response<ResponseBody> response = call.execute();
            if (response.isSuccessful()) {
                String resStr = response.body().string();
                res = JSONObject.parseObject(resStr, clazz);
            } else {
                String result = response.errorBody().string();
                res = clazz.newInstance();
                res.setCode(99L);
                res.setMessage("请求失败:" + result);
            }
        } catch (Exception e) {
            try {
                res = clazz.newInstance();
                res.setCode(99L);
                res.setMessage("系统错误:" + e.getMessage());
            } catch (InstantiationException e1) {
            } catch (IllegalAccessException e1) {
            }
        }

        return res;
    }

    private static TrustManager[] buildTrustManagers() {
        return new TrustManager[] {
            new X509TrustManager() {
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[]{};
                }
            }
        };
    }

    private String getBaseUrl(Class clazz) {
        if (clazz == null) {
            throw new RuntimeException("service class cannot be null");
        }
        BaseUrl annotation = (BaseUrl) clazz.getAnnotation(BaseUrl.class);
        if (annotation == null) {
            throw new RuntimeException("service class must be annotated by @BaseUrl to designate base-url");
        }
        String value = annotation.value();
        if (StringUtils.isBlank(value)) {
            throw new RuntimeException("no valid base-url designated");
        }
        return value;
    }

}
