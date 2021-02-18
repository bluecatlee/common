package com.github.bluecatlee.common.datasource.dynamic.jdbc;

import com.github.bluecatlee.common.datasource.dynamic.core.DataSourceContextHolder;
import com.github.bluecatlee.common.datasource.dynamic.core.DynamicDataSource;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;

/**
 * 动态JdbcTemplate工具
 *      由于JdbcTemplate本身就是对DataSource的包装，所以只要将动态数据源设置到JdbcTemplate，获取时指定路由键即可动态获取
 *      该工具可以可以在dao层注入，层次更清晰
 */
@Service
public class JdbcTemplateUtil /*implements InitializingBean*/ {

    @Resource(name = "dynamicDataSource")
    DynamicDataSource dataSource;

//    @Override
//    public void afterPropertiesSet() throws Exception {
//        Assert.notNull(dataSource, "DynamicDataSource cannot be null");
//    }

    public JdbcTemplate getJdbcTemplate(String dataSourceType) {
        DataSourceContextHolder.setDataSourceType(dataSourceType);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource);
        return jdbcTemplate;
    }

}
