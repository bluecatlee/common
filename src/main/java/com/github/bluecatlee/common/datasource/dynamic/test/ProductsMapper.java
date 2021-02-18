package com.github.bluecatlee.common.datasource.dynamic.test;

import com.github.bluecatlee.common.datasource.dynamic.mybatis.DynamicDataSource;
import tk.mybatis.mapper.common.Mapper;

/**
 * Created by 胶布 on 2021/2/18.
 */
//@DynamicDataSource(type = "datasource1")
public interface ProductsMapper extends Mapper<Products> {
}
