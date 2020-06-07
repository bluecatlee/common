package com.github.bluecatlee.common.rxjava.httpclient;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.protocol.HttpContext;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;

/**
 * Created by 胶布 on 2020/6/6.
 */
public class RetryHandler implements HttpRequestRetryHandler{

    @Override
    public boolean retryRequest(IOException exception, int executionCount, HttpContext httpContext) {
        // 如果已经重试了3次就放弃
        if (executionCount >= 3) {
            return false;
        }
        // 如果服务器丢掉了连接，就重试
        if (exception instanceof NoHttpResponseException) {
            return true;
        }
        // 目标服务器不可达
        if (exception instanceof UnknownHostException) {
            return false;
        }
        // 不重试SSL握手异常
        if (exception instanceof SSLHandshakeException) {
            return false;
        }
        if (exception instanceof SSLException) {
            return false;
        }
        // 连接超时不重试
        if (exception instanceof ConnectTimeoutException) {
            return false;
        }
        // 超时
        if (exception instanceof InterruptedIOException) {
            return true;
        }
        HttpClientContext clientContext = HttpClientContext.adapt(httpContext);
        HttpRequest request = clientContext.getRequest();
        // 如果请求是幂等的则可以重试
        if (!(request instanceof HttpEntityEnclosingRequest)) {
            return true;
        }
        return false;
    }

}
