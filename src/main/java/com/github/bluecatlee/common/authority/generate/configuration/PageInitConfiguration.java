package com.github.bluecatlee.common.authority.generate.configuration;

import com.github.bluecatlee.common.authority.generate.annotation.PageAnnotation;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * page数据生成
 */
@Component
public class PageInitConfiguration implements ApplicationRunner {

    private static final Logger dejavu = LoggerFactory.getLogger(PageInitConfiguration.class);

    @Autowired
    @Qualifier("requestMappingHandlerMapping")
    private RequestMappingHandlerMapping mapping;

    @Data
    private class Page {
        /**
         * 页面名称
         */
        private String pageName;
        /**
         * 页面url
         */
        private String pageUrl;
        /**
         * 页面父id
         */
        private Long parentid;
        /**
         * 请求方法
         */
        private MethodType method;
    }

    private enum MethodType{

        GET(1),

        POST(2);

        private int value;

        MethodType(int value) {
            this.value = value;
        }

        public static MethodType valueOf(int value) {
            for (MethodType methodType : MethodType.values()) {
                if (methodType.value == value) {
                    return methodType;
                }
            }
            return null;
        }

        public int getIntValue() {
            return value;
        }
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {
        List list = prepareData();
        if (list != null && !list.isEmpty()) {
            try {
                // todo: generate method unimplemented, such as storing in db
            } catch (Exception e) {
                dejavu.warn("INIT PAGES DATA FAILED, DETAIL INFO [{}]", e.getMessage());
            }
        } else {
            // dejavu.info("PREPARE PAGES DATA FAILED, CHECK ANNOTATION CONFIGS");
        }

    }

    private List prepareData() {
        Map<RequestMappingInfo, HandlerMethod> map = mapping.getHandlerMethods();

        List<Page> list = new ArrayList<>();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> m : map.entrySet()) {
            RequestMappingInfo info = m.getKey();
            HandlerMethod method = m.getValue();

            PageAnnotation methodAnnotation = method.getMethodAnnotation(PageAnnotation.class);
            if (methodAnnotation == null) {
                continue;
            }
            Page page = new Page();
            page.setPageName(methodAnnotation.name());
            page.setParentid(methodAnnotation.parentId());

            PatternsRequestCondition p = info.getPatternsCondition();
            for (String url : p.getPatterns()) {
                page.setPageUrl(url);
            }
            RequestMethodsRequestCondition methodsCondition = info.getMethodsCondition();
            for (RequestMethod requestMethod : methodsCondition.getMethods()) {
                page.setMethod(MethodType.valueOf(requestMethod.toString().toUpperCase()));
            }

            list.add(page);
        }

        return list;
    }
}
