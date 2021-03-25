package com.github.bluecatlee.common.datasource.dynamic.test;

import com.github.bluecatlee.common.datasource.dynamic.core.DataSourceContextHolder;
import com.github.bluecatlee.common.datasource.dynamic.core.DynamicDataSource;
import com.github.bluecatlee.common.datasource.dynamic.jdbc.JdbcTemplateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.sql.DataSource;
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

    @Autowired
    private DataSourceTransactionManager transactionManager;

    @GetMapping("/testDynamicDataSource")
    public void testDynamicDataSource() {

        System.out.println("start...");

        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        definition.setTimeout(300);
        definition.setIsolationLevel(2);
        definition.setPropagationBehavior(3);
        DefaultTransactionDefinition definition2 = new DefaultTransactionDefinition();
        definition.setTimeout(300);
        definition.setIsolationLevel(2);
        definition.setPropagationBehavior(3);


        TransactionStatus transaction = transactionManager.getTransaction(definition);

        JdbcTemplate jt1 = jdbcTemplateUtil.getJdbcTemplate("dataSource1");
//        String dataSourceType = DataSourceContextHolder.getDataSourceType();
//        System.out.println(dataSourceType);
//        DataSource dataSource = transactionManager.getDataSource();
//        if(dataSource instanceof DynamicDataSource) {
//            System.out.println("dynamicdatasource");
//        }

//        jt1.execute("select * from actor");
        jt1.execute("insert into actor values (1012, \"李\", \"蓝猫\", \"2021-02-12 10:26:00\" )");
        transactionManager.commit(transaction);

        System.out.println("===========================");





        TransactionStatus transaction2 = transactionManager.getTransaction(definition2);
        JdbcTemplate jt2 = jdbcTemplateUtil.getJdbcTemplate("dataSource2");

//        String dataSourceType2 = DataSourceContextHolder.getDataSourceType();
//        System.out.println(dataSourceType2);

//        jt2.execute("select * from actor");
        jt2.execute("INSERT INTO products VALUES (1012, \"ttttt\", NULL ,NULL ,NULL )");

        transactionManager.commit(transaction2);

        System.out.println("finished");

    }

    @GetMapping("/testDynamicDataSource2")
    public void testDynamicDataSource2() {

//        DataSourceContextHolder.setDataSourceType("dataSource1");
//
//        List<Actor> actors = actorMapper.selectAll();
//        System.out.println(actors.size());
//
//        DataSourceContextHolder.setDataSourceType("dataSource2");
//
//        List<Products> products = productsMapper.selectAll();
//        System.out.println(products.size());

        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        definition.setTimeout(300);
        definition.setIsolationLevel(2);
        definition.setPropagationBehavior(3);


        TransactionStatus transaction = transactionManager.getTransaction(definition);

        DataSourceContextHolder.setDataSourceType("dataSource1");
        Actor actor = new Actor();
        actor.setActorId(1003);
        actor.setFirstName("李");
        actor.setLastName("蓝猫");
        actorMapper.insertSelective(actor);


        DataSourceContextHolder.setDataSourceType("dataSource2");
        Products products = new Products();
        products.setId(1001);
        products.setProductName("商品1111");
        products.setProductNumber(1);
        products.setProductPrice(2d);
        productsMapper.insertSelective(products);

        transactionManager.commit(transaction);

        System.out.println("finished...");
    }

}
