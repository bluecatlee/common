package com.github.bluecatlee.common.pay.service.shengfutong;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 盛付通签名排序使用，下标从1开始，连续
 *
 */
@Target(value = ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SignParam {

	/**
	 * 索引
	 */
	int value();

	/**
	 * 名称
	 * 
	 * @return
	 */
	String name() default "";

}
