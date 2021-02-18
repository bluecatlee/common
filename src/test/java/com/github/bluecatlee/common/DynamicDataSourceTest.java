package com.github.bluecatlee.common;

import com.github.bluecatlee.common.datasource.dynamic.jdbc.JdbcTemplateUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

/**
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CommonApplication.class)
public class DynamicDataSourceTest {

    @Autowired
    private JdbcTemplateUtil jdbcTemplateUtil;

    /**
     * fixme spring容器启动之后，该单元测试没有执行
     */
    @Test
    public void testDynamicDataSource() {

        JdbcTemplate jt1 = jdbcTemplateUtil.getJdbcTemplate("dataSource1");
        jt1.execute("select * from actor");

        JdbcTemplate jt2 = jdbcTemplateUtil.getJdbcTemplate("dataSource2");
        jt2.execute("select * from actor");

    }


}
