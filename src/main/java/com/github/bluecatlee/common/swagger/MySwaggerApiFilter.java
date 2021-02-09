//package com.github.bluecatlee.common.swagger;
//
//import org.springframework.web.method.HandlerMethod;
//import springfox.documentation.RequestHandler;
//import springfox.documentation.spring.web.WebMvcRequestHandler;
//
//public class MySwaggerApiFilter implements SwaggerApiFilter {
//
//    @Override
//    public boolean apply(RequestHandler input) {
//
//        if (input instanceof WebMvcRequestHandler) {
//            WebMvcRequestHandler handler = (WebMvcRequestHandler)input;
//            HandlerMethod handlerMethod = handler.getHandlerMethod();
//            // 已经拿到Handler和HandlerMethod了，尽情根据实际情况过滤吧。
//            // 返回false表示不通过，返回true表示通过
//        }
//        return true;
//    }
//}