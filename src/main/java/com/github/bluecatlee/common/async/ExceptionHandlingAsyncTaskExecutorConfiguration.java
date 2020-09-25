package com.github.bluecatlee.common.async;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@EnableAsync
// @Configuration
public class ExceptionHandlingAsyncTaskExecutorConfiguration {

    @Bean
    public TaskExecutor taskExecutor() {
        // ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();  // 推荐 实质是对java.util.concurrent.ThreadPoolExecutor的包装
        // executor.setCorePoolSize(10);
        // executor.setMaxPoolSize(20);
        // executor.setQueueCapacity(200);
        // executor.setKeepAliveSeconds(60);
        // executor.setThreadNamePrefix("taskExecutor-");
        // executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor();   // 非线程池 每次都创建新的线程 使用@Async不指定线程池名称时的默认实现
        return new ExceptionHandlingAsyncTaskExecutor(executor);
    }

}
