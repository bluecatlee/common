package com.github.bluecatlee.common.datasource.dynamic.mybatis;

import java.lang.annotation.*;

/**
 * Created by 胶布 on 2021/2/18.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Documented
public @interface DynamicDataSource {
    String type();
}
