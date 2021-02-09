package com.github.bluecatlee.common.filter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 解决swagger请求跨域
 * @see <a href="https://github.com/bluecatlee/scripts/blob/master/nginx/conf/nginx.conf">nginx代理层解决方案</a>
 */
@Component
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-02-08T09:11:31.724Z")
public class ApiOriginFilter implements Filter {

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    HttpServletResponse res = (HttpServletResponse) response;

    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpServletResponse httpResponse = (HttpServletResponse) response;

    res.addHeader("Access-Control-Allow-Credentials", "true");    // 是否允许发送cookie

    String origin = httpRequest.getHeader("Origin");
    if (StringUtils.isNotEmpty(origin)) {
      if (origin.startsWith("http")) {
        res.addHeader("Access-Control-Allow-Origin", origin);
      } else {
        res.addHeader("Access-Control-Allow-Origin", "*");
      }
    } else {
      res.addHeader("Access-Control-Allow-Origin", "*");
    }

    res.addHeader("Access-Control-Allow-Headers", "Content-Type, Cookies, x-requested-with");
    chain.doFilter(request, response);
  }

  @Override
  public void destroy() {
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }

}