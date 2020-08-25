package com.github.bluecatlee.common;

import com.github.bluecatlee.common.configuration.CustomAutoConfigurationImportSelector;
import org.activiti.application.conf.ApplicationProcessAutoConfiguration;
import org.activiti.core.common.spring.identity.config.ActivitiSpringIdentityAutoConfiguration;
import org.activiti.runtime.api.conf.CommonRuntimeAutoConfiguration;
import org.activiti.runtime.api.conf.ConnectorsAutoConfiguration;
import org.activiti.runtime.api.conf.ProcessRuntimeAutoConfiguration;
import org.activiti.runtime.api.conf.TaskRuntimeAutoConfiguration;
import org.activiti.spring.boot.EndpointAutoConfiguration;
import org.activiti.spring.boot.ProcessEngineAutoConfiguration;
import org.activiti.spring.process.conf.ProcessExtensionsAutoConfiguration;
import org.activiti.spring.process.conf.ProcessExtensionsConfiguratorAutoConfiguration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.Import;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        // 取消activiti的自动配置 activiti-spring-boot-starter 7.1.0.M6版本可以取消 低版本不行
        // ProcessEngineAutoConfiguration.class,
        // ApplicationProcessAutoConfiguration.class,
        // ActivitiSpringIdentityAutoConfiguration.class,
        // CommonRuntimeAutoConfiguration.class,
        // ConnectorsAutoConfiguration.class,
        // EndpointAutoConfiguration.class,
        // ProcessExtensionsAutoConfiguration.class,
        // ProcessExtensionsConfiguratorAutoConfiguration.class,
        // ProcessRuntimeAutoConfiguration.class,
        // TaskRuntimeAutoConfiguration.class,
        // UserDetailsServiceAutoConfiguration.class
})
// 自定义配置导入选择器来取消activiti的自动配置
@Import(CustomAutoConfigurationImportSelector.class)
@EnableSwagger2
public class CommonApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommonApplication.class, args);
    }

}

