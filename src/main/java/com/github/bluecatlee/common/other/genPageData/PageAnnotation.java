package com.github.bluecatlee.common.other.genPageData;

import java.lang.annotation.*;

/**
 * 权限接口注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface PageAnnotation {
    String name(); // 名称
    long parentId(); // 父id
}
