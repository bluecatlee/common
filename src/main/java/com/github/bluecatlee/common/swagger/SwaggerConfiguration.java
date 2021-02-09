package com.github.bluecatlee.common.swagger;

import com.google.common.base.Predicate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.WebMvcRequestHandler;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * swagger文档插件配置
 */
@Configuration
public class SwaggerConfiguration {

    @Bean
    public Docket RestApi() {

        Docket docket = new Docket(DocumentationType.SWAGGER_2).apiInfo(new ApiInfoBuilder().title("文档").
                description("文档").build())
                .select()
//                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .apis(RequestHandlerSelectors.any())    // 对所有request handler都生成文档 不需要指定相关的文档注解
//                .apis(filterRequestMethod())
                .paths(PathSelectors.any())
                .build()
                .useDefaultResponseMessages(false)
                .globalOperationParameters(setHeaderToken());   // 添加全局的默认参数

        return docket;
    }

    /**
     * 请求头添加Authorization参数
     * @return
     */
    private List<Parameter> setHeaderToken() {
        ParameterBuilder tokenPar = new ParameterBuilder();
        List<Parameter> pars = new ArrayList<>();
        tokenPar.name("Authorization").description("token").modelRef(new ModelRef("string")).parameterType("header").required(false).build();
        pars.add(tokenPar.build());
        return pars;
    }

    /**
     * 只有GET POST请求才能生成文档
     *      todo 对于使用@RequestMapping注解且未指定method的handler 如何过滤掉其他请求方法？
     * @return
     */
    private Predicate<RequestHandler> filterRequestMethod() {
        Predicate<RequestHandler> predicate = input -> {
            if (input instanceof WebMvcRequestHandler) {
                WebMvcRequestHandler handler = (WebMvcRequestHandler)input;
                RequestMappingInfo requestMapping = handler.getRequestMapping();
                RequestMethodsRequestCondition methodsCondition = requestMapping.getMethodsCondition();
                Set<RequestMethod> methods = methodsCondition.getMethods();
                if (methods.contains(RequestMethod.GET) || methods.contains(RequestMethod.POST) /*|| methods.isEmpty()*/) {
                    return true;
                }
            }
            return false;
        };
        return predicate;
    }

}
