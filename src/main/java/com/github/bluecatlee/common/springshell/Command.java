package com.github.bluecatlee.common.springshell;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@ShellComponent
public class Command {

    @Autowired
    @Qualifier("requestMappingHandlerMapping")
    private RequestMappingHandlerMapping mapping;

    @ShellMethod(value = "Get mappings of request url and handler", key = {"map"})
    public String mapping(boolean all) {
        Map<RequestMappingInfo, HandlerMethod> map = mapping.getHandlerMethods();

        Map<String, String> result = new HashMap<>();

        for (Map.Entry<RequestMappingInfo, HandlerMethod> m : map.entrySet()) {
            RequestMappingInfo info = m.getKey();
            HandlerMethod method = m.getValue();

            Set<String> patterns = info.getPatternsCondition().getPatterns();

            if (all) {
                result.put(info.toString(), method.toString());
            } else {
                String name = method.getBeanType().getName();
                // 路径与handler的简单映射
                patterns.forEach(s -> result.put(s, name));
            }

        }

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String s = objectMapper.writeValueAsString(result);
            return s;
        } catch (JsonProcessingException e) {
            // e.printStackTrace();
            return e.getMessage();
        }

    }

}
