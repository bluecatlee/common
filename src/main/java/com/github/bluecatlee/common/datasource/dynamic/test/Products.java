package com.github.bluecatlee.common.datasource.dynamic.test;

import lombok.Data;

import javax.persistence.Table;

/**
 * Created by 胶布 on 2021/2/18.
 */
@Data
@Table(name = "products")
public class Products {

    private Integer id;
    private String productName;
    private Double productPrice;
    private Integer productNumber;
    private String productCommon;

}
