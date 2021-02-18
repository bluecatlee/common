package com.github.bluecatlee.common.datasource.dynamic.mybatis;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by 胶布 on 2021/2/18.
 */
@Configuration
//@MapperScan(basePackages = {"com.github.bluecatlee.dynamicdatasource.mapper"}, sqlSessionFactoryRef = "sqlSessionFactory")
@tk.mybatis.spring.annotation.MapperScan(basePackages={"com.github.bluecatlee.common.datasource.dynamic.test","tk.mybatis.mapper.common.Mapper"}, sqlSessionTemplateRef  = "sqlSessionTemplate")
public class MybatisExtConfig {

    @Bean(name = "sqlSessionTemplate")
    public SqlSessionTemplate paymentSqlSessionTemplate(@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

}
