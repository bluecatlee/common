package com.github.bluecatlee.common.duplicateSubmit.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface DuplicateSubmitAnnotation {

    /**
     * 类型 (扩展: 可以定义成枚举类型)
     */
    String type();

    /**
     * 是否保存token
     */
    boolean saveStoken() default false;

    /**
     * 是否移除token
     */
    boolean removeStoken() default false;

}
