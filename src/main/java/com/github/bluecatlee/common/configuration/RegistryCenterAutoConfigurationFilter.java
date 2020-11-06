package com.github.bluecatlee.common.configuration;

import org.springframework.boot.autoconfigure.AutoConfigurationImportFilter;
import org.springframework.boot.autoconfigure.AutoConfigurationMetadata;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *  排除注册中心的自动配置 存在多注册中心依赖时可以使用 (不推荐 因为需要知道所有相关的自动配置类 以及排除掉某些自动配置类会缺失部分功能)
 *  结合spring-factories机制 这种方式也是一种排除自动配置类的方式
 */
@Deprecated
public class RegistryCenterAutoConfigurationFilter implements AutoConfigurationImportFilter {

    private static final Set<String> SHOULD_SKIP = new HashSet<>(Arrays.asList(
                //排除服务注册的自动配置
                //"org.springframework.cloud.client.serviceregistry.ServiceRegistryAutoConfiguration",
                //"org.springframework.cloud.client.serviceregistry.AutoServiceRegistrationAutoConfiguration",

                //仅排除consul相关自动配置 可以直接用spring.cloud.consul.enabled=false的方式
                "org.springframework.cloud.consul.discovery.ConsulCatalogWatchAutoConfiguration",
                "org.springframework.cloud.consul.discovery.ConsulDiscoveryClientConfiguration",
                "org.springframework.cloud.consul.serviceregistry.ConsulAutoServiceRegistrationAutoConfiguration",
                "org.springframework.cloud.consul.serviceregistry.ConsulServiceRegistryAutoConfiguration"

             ));


    @Override
    public boolean[] match(String[] autoConfigurationClasses, AutoConfigurationMetadata autoConfigurationMetadata) {
        boolean[] matches = new boolean[autoConfigurationClasses.length];
        for (int i = 0; i < autoConfigurationClasses.length; i++) {
            matches[i] = !SHOULD_SKIP.contains(autoConfigurationClasses[i]);
        }
        return matches;
    }

}
