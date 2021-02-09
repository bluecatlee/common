package com.github.bluecatlee.common.configuration;

import com.github.bluecatlee.common.interceptor.ExecTimeCostInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfg implements WebMvcConfigurer {

    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(new ExecTimeCostInterceptor()).addPathPatterns("/**");
    }

}
