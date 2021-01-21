package com.github.bluecatlee.common.retrofit.annotation;

import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface BaseUrl {
    String value();
}
