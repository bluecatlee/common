package com.github.bluecatlee.common.exception;

import com.github.bluecatlee.common.duplicateSubmit.annotation.DuplicateSubmitAnnotation;
import com.github.bluecatlee.common.duplicateSubmit.utils.DuplicateKeyUtils;
import com.github.bluecatlee.common.redis.jedis.RedisCache;
import com.github.bluecatlee.common.restful.RestResult;
import com.github.bluecatlee.common.spring.editor.DoubleEditor;
import org.apache.commons.lang3.StringUtils;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Executable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 全局异常及响应增强处理器
 */
@ControllerAdvice
// 如果使用@RestControllerAdvice 需要返回json的时候异常处理方法上就可以不用加@ResponseBody
public class AdviceController implements ResponseBodyAdvice {

    private final Logger   logger = LoggerFactory.getLogger(AdviceController.class);

    @Autowired
    private RedisCache cache;

    /**
     * 绑定请求参数到指定的属性编辑器
     * @param binder
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields("ignore"); // 忽略form表单中名为ignore的字段的绑定 支持通配符

        CustomDateEditor dateEditor = new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"),true);
        binder.registerCustomEditor(Date.class, dateEditor);  // 将前端传递的时间格式字符串直接绑定到Date类型

        // 说明: jackson直接序列化反序列化Date类型会有时差产生 因为会获取当前系统的时区进行处理 (不建议直接传Date类型的数据)
        // 解决方案: 1). 局部处理, 字段加注解@JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8")  只能处理Bean中的Date类型
        //           2). 全局处理, 设置jackson的时区为GMT+8

        DoubleEditor doubleEditor = new DoubleEditor();
        binder.registerCustomEditor(Double.class, doubleEditor);  // 注册自定义的Double编辑器 实测json格式传参时无效 todo

    }


    /**
     * 异常处理
     *      @ExceptionHandler可以直接指定异常类型 为每个异常写个异常处理方法
     * @param exception
     * @param request
     * @param response
     * @return
     */
    @ExceptionHandler
    @ResponseBody
    public Object exception(Exception exception, HttpServletRequest request,
                                HttpServletResponse response) {
        if (exception instanceof OAuthProblemException
                || exception instanceof OAuthSystemException) {
            logger.warn("OAuthException", exception.getMessage());
            return RestResult.LOGIN().build();

            // 参数校验异常 MethodArgumentNotValidException (外层异常是BindException 但是BindException不一定都是参数校验异常)
        } else if (exception instanceof BindException || exception instanceof MethodArgumentNotValidException) {
            logger.warn("BindException", exception.getMessage());
            BindingResult bindingResult = null;
            if (exception instanceof BindException) {
                bindingResult = ((BindException) exception).getBindingResult();
            } else {
                bindingResult = ((MethodArgumentNotValidException) exception).getBindingResult();
            }
            if (bindingResult != null && bindingResult.hasErrors()) {
                List<FieldError> list = bindingResult.getFieldErrors();
                if (list != null && list.size() > 0) {
                    FieldError fieldError = list.get(0);
                    return RestResult.ERROR_PARAMS().message(fieldError.getDefaultMessage())
                            .build();
                }
            }
            return RestResult.ERROR_SERVER().message("参数校验失败").build();
        } else if (exception instanceof CommonException) {

            CommonExceptionEnum resultCode = ((CommonException) exception).getResultCode();
            if (CommonExceptionEnum.AUTHORITY_DENIED.equals(resultCode)) {
                return RestResult.FORBIDDEN().build();
            }
            // logger.error("============业务异常=========", com.github.bluecatlee.common.pay.exception);
            return RestResult.ERROR_SERVER().message(exception.getMessage()).build();
        } else {
            logger.error("Catch All Exception", exception);
        }
        return RestResult.ERROR_SERVER().message("服务器正忙，请稍后在试").build();
    }

    /**
     * 定义model属性
     * @param request
     * @return
     */
    @ModelAttribute("test")
    public String test(HttpServletRequest request) {
        return "";
    }


    /**
     * 获取ip的方式
     * @param request
     * @return
     */
    @ModelAttribute("ip")
    @SuppressWarnings("all")
    public String getIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (StringUtils.isBlank(ip) || StringUtils.equalsIgnoreCase("unknown", ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || StringUtils.equalsIgnoreCase("unknown", ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || StringUtils.equalsIgnoreCase("unknown", ip)) {
            ip = request.getRemoteAddr();
        }
        if (StringUtils.isBlank(ip)) {
            ip = "0.0.0.0";
        }
        return ip;
    }

    /**
     * 定义需要进行响应值处理的策略
     * @param methodParameter
     * @param aClass
     * @return
     */
    @Override
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        return true;
    }

    /**
     * 响应值处理
     * @param o
     * @param methodParameter
     * @param mediaType
     * @param aClass
     * @param serverHttpRequest
     * @param serverHttpResponse
     * @return
     */
    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        try {
            if (o instanceof RestResult) {
                // Object attribute = ((HttpServletRequest) serverHttpRequest).getAttribute(Constants.ACCOUNT_AUDIT_STATUS);
                RestResult restResult = (RestResult) o;

                Executable executable = methodParameter.getExecutable();
                DuplicateSubmitAnnotation annotation = executable.getAnnotation(DuplicateSubmitAnnotation.class);
                if (annotation != null && annotation.saveStoken() && StringUtils.isNotBlank(annotation.type())) {
                    String uuid = UUID.randomUUID().toString();
                    restResult.setSToken(uuid);
                    String key = DuplicateKeyUtils.getKey(annotation.type(), uuid);
                    int exp = 3 * 24 * 3600;  // 设置过期时间
                    cache.put(key, uuid, exp);
                }
                return restResult;
            }
        } catch (Exception e) {
            return o;
        }

        return o;
    }
}