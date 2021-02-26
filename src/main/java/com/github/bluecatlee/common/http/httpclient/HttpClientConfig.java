package com.github.bluecatlee.common.http.httpclient;

import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpClientConfig {

//    @Bean
//    public HttpClientConnectionManager httpClientConnectionManager() {
//        return new PoolingHttpClientConnectionManager();
//    }
//
//    @Bean
//    public ExpiredConnectionEvictor expiredConnectionEvictor(HttpClientConnectionManager connectionManager) {
//        return new ExpiredConnectionEvictor(connectionManager);
//    }
//
//    @Bean
//    public ExpiredConnectionEvictor expiredConnectionEvictor() {
//        MultiThreadedHttpConnectionManager multiThreadedHttpConnectionManager = HttpUtil.getMultiThreadedHttpConnectionManager();
//        return new ExpiredConnectionEvictor(multiThreadedHttpConnectionManager);   // todo 桥接上
//    }

//    /**
//     * httpclient自带了清除空闲连接的机制
//     */
//    @Bean
//    public IdleConnectionTimeoutThread idleConnectionTimeoutThread() {
//        IdleConnectionTimeoutThread idleConnectionTimeoutThread = new IdleConnectionTimeoutThread();
//        idleConnectionTimeoutThread.start();
//        return idleConnectionTimeoutThread;
//    }

}
