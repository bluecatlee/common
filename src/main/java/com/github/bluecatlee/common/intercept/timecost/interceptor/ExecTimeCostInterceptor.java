package com.github.bluecatlee.common.intercept.timecost.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.NamedThreadLocal;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Slf4j
public class ExecTimeCostInterceptor implements HandlerInterceptor {
    private NamedThreadLocal<Long> execTimeThreadLocal = new NamedThreadLocal<Long>("ExecTimeCost");

	@Override
	public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {
		execTimeThreadLocal.set(System.currentTimeMillis());//线程绑定变量（该数据只有当前请求的线程可见）  
        return true;
	}

	@Override
	public void postHandle(HttpServletRequest request,
                           HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
	}

	@Override
	public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
	        long beginTime = execTimeThreadLocal.get();//得到线程绑定的局部变量（开始时间）  
	        log.info(String.format("Execute Controller [%s]!200 consume %d ms", request.getRequestURI(), System.currentTimeMillis() - beginTime));
	}

}
