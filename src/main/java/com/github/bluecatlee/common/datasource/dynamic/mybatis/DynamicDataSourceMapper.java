package com.github.bluecatlee.common.datasource.dynamic.mybatis;

import com.github.bluecatlee.common.datasource.dynamic.core.DataSourceContextHolder;
import tk.mybatis.mapper.common.Mapper;

/**
 * 通用的动态mapper
 *      方式1.继承的方式。实现接口重新继承通用接口。重写所有方法 在底层方法调用前先设置数据源类型
 *      方式2.类中封装的方式。内部持有具体实现。依然是在底层方法调用前线设置数据源类型。
 *
 * Created by 胶布 on 2021/2/18.
 */
@Deprecated
public interface DynamicDataSourceMapper<T> extends Mapper<T> {

    default T selectOne() {
        DataSourceContextHolder.setDataSourceType("xx");
        return this.selectOne();
    }

    // ...

}
