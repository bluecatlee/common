package com.github.bluecatlee.common.datasource.dynamic.test;

import com.github.bluecatlee.common.datasource.dynamic.core.DataSourceContextHolder;
import com.github.bluecatlee.common.datasource.dynamic.jdbc.JdbcTemplateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by 胶布 on 2021/2/18.
 */
@RestController
public class TestSuit {

    @Autowired
    private JdbcTemplateUtil jdbcTemplateUtil;

    @Autowired
    private ActorMapper actorMapper;

    @Autowired
    private ProductsMapper productsMapper;

    @GetMapping("/testDynamicDataSource")
    public void testDynamicDataSource() {

        System.out.println("start...");

        JdbcTemplate jt1 = jdbcTemplateUtil.getJdbcTemplate("dataSource1");
        jt1.execute("select * from actor");

        JdbcTemplate jt2 = jdbcTemplateUtil.getJdbcTemplate("dataSource2");
//        jt2.execute("select * from actor");
        jt2.execute("select * from products");

        System.out.println("finished");

    }

    @GetMapping("/testDynamicDataSource2")
    public void testDynamicDataSource2() {

        DataSourceContextHolder.setDataSourceType("dataSource1");

        List<Actor> actors = actorMapper.selectAll();
        System.out.println(actors.size());

        DataSourceContextHolder.setDataSourceType("dataSource2");

        List<Products> products = productsMapper.selectAll();
        System.out.println(products.size());

        System.out.println("finished...");
    }

}
