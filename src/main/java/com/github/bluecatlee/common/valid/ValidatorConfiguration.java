package com.github.bluecatlee.common.valid;

import org.hibernate.validator.HibernateValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 * hibernate-validator 快速失败模式
 */
@Configuration
public class ValidatorConfiguration {

    /**
     * todo
     *      如果引入了spring-cloud-stream的依赖 会存在循环引用的问题
     *      自动创建的BindingHandlerAdvise Bean需要注入Validator Bean 因而依赖WebMvcProperties的初始化
     *      而WebMvcProperties的初始化依赖BindingServiceConfiguration-BindingHandlerAdvise的初始化
     */
    @Bean
    public Validator validator(){
        ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)
                .configure()
                // .addProperty( "hibernate.validator.fail_fast", "true" )
                .failFast(true)
                .buildValidatorFactory();
        Validator validator = validatorFactory.getValidator();

        return validator;
    }

}
