package com.github.bluecatlee.common.duplicateSubmit.utils;

import com.github.bluecatlee.common.exception.CommonException;
import com.github.bluecatlee.common.exception.CommonExceptionEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;

public class DuplicateKeyUtils {

    private static final String prefix = "Bearer ";  // OAuth2.0认证方式值前缀

    /**
     * 获取key
     *      规则： 前缀+会话token值+业务类型+uuid (会话token值按照实际需求修改)
     * @param type
     * @param uuid
     * @return
     */
    public static String getKey(String type, String uuid) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
        String token = request.getHeader("Authorization");
        if (StringUtils.isBlank(token) || !token.startsWith(prefix)) {
            throw new CommonException(CommonExceptionEnum.AUTHORITY_DENIED);
        }
        token = token.substring(prefix.length());
        return new StringBuilder("_duplicate_key").append("_").append(token).append("_").append(type).append("_").append(uuid).toString();
    }

}
