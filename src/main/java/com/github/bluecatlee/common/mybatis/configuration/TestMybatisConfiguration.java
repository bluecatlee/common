package com.github.bluecatlee.common.mybatis.configuration;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import tk.mybatis.spring.annotation.MapperScan;

import javax.annotation.Resource;
import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@Slf4j
@MapperScan(basePackages={"com.github.bluecatlee.common.test.mapper","tk.mybatis.mapper.common.Mapper"}, sqlSessionTemplateRef  = "testSqlSessionTemplate")
public class TestMybatisConfiguration {

    @Resource(name="testDataSource")
    public DataSource testDataSource;


    @Bean(name="testSqlSessionFactory")
    public SqlSessionFactory paymentSqlSessionFactory() {
        try {

            SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
            sessionFactory.setDataSource(testDataSource);
            sessionFactory.setTypeAliasesPackage("com.github.bluecatlee.common.test.entity");
            sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver()
                    .getResources("classpath*:mybatis/mappers/*Mapper.xml"));
            sessionFactory.setConfigLocation(new DefaultResourceLoader()
                    .getResource("mybatis/mybatis-config.xml"));
            return sessionFactory.getObject();
        } catch (Exception e) {
            log.error("Could not confiure mybatis session factory",e);
            return null;
        }
    }

    @Bean(name = "testSqlSessionTemplate")
    public SqlSessionTemplate paymentSqlSessionTemplate(@Qualifier("testSqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Bean(name="testTransactionManager")
    public DataSourceTransactionManager transactionManager() {
        return new DataSourceTransactionManager(testDataSource);
    }
}