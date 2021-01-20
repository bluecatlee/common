package com.github.bluecatlee.common.mybatis.configuration;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
//@ConfigurationProperties(prefix = "db.test")
public class TestDataSourceConfig {

    @Bean("testDataSource")
//    @ConfigurationProperties("db.test")
    public DataSource dataSource() {
        DruidDataSource datasource = new DruidDataSource(); // 结合配置中心 可以避免在项目代码中显示指定数据库账户密码信息
        return datasource;
    }

}
