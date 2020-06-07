package com.github.bluecatlee.common.rxjava.httpclient;

import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Created by 胶布 on 2020/6/6.
 */
public class RxHttpClient {

    // 连接池
    private static final PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();

    static {
        // 设置最大连接数
        connManager.setMaxTotal(200);
        // 设置每个连接的路由数
        connManager.setDefaultMaxPerRoute(20);
    }

    // 获取客户端连接对象
    public static CloseableHttpClient getHttpClient(int timeout) {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(timeout)
                .setConnectTimeout(timeout)
                .setSocketTimeout(timeout)
                .build();
        return HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .setRetryHandler(new RetryHandler())
                .setConnectionManager(connManager)
                .build();
    }

    public static Maybe<String> httpGet(String url, int timeout) {
        return Maybe.create(new MaybeOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull MaybeEmitter<String> maybeEmitter) throws Exception {
                maybeEmitter.onSuccess(url);
            }
        }).map(new Function<String, String>() {
            @Override
            public String apply(@NonNull String s) throws Exception {
                CloseableHttpClient httpClient = getHttpClient(timeout);
                HttpGet httpGet = new HttpGet(url);
                CloseableHttpResponse response = null;
                String msg = null;
                try {
                    response = httpClient.execute(httpGet);
                    HttpEntity entity = response.getEntity();
                    msg = EntityUtils.toString(entity, "UTF-8");
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                } catch(IOException e) {
                    e.printStackTrace();
                } finally {
                    if (response != null) {
                        try {
                            EntityUtils.consume(response.getEntity());
                            response.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return msg;
            }
        });
    }

    public static void main(String[] args){
        httpGet("http://www.baidu.com", 5000).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                System.out.println(s);
            }
        });
    }

}
