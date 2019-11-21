package com.github.bluecatlee.common.duplicateSubmit.aop;

import com.github.bluecatlee.common.duplicateSubmit.annotation.DuplicateSubmitAnnotation;
import com.github.bluecatlee.common.duplicateSubmit.constants.Constants;
import com.github.bluecatlee.common.duplicateSubmit.utils.DuplicateKeyUtils;
import com.github.bluecatlee.common.exception.CommonException;
import com.github.bluecatlee.common.exception.CommonExceptionEnum;
import com.github.bluecatlee.common.redis.RedisCache;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class DuplicateSubmitAspect {

    public static class DuplicateSubmitException extends CommonException {
        public DuplicateSubmitException(CommonExceptionEnum resultCode, String message) {
            super(resultCode, message);
        }
    }

    private final org.slf4j.Logger Logger = LoggerFactory.getLogger(DuplicateSubmitAspect.class);

    @Autowired
    private RedisCache cache;

    @Pointcut("@annotation(com.github.bluecatlee.common.duplicateSubmit.annotation.DuplicateSubmitAnnotation)")
    public void point() {
    }

    @Before("point() && @annotation(annotation)")
    public void before(final JoinPoint joinPoint, DuplicateSubmitAnnotation annotation) {
        if (annotation != null && annotation.removeStoken() && StringUtils.isNotBlank(annotation.type())) {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
            String sToken = request.getHeader(Constants.DUPLICATE_SUBMIT_TOKEN);
            if (StringUtils.isBlank(sToken)) {
                throw new CommonException(CommonExceptionEnum.ILLEGAL_ARGUMENT, "缺少sToken");
            }
            String key = DuplicateKeyUtils.getKey(annotation.type(), sToken);
            Long remove = cache.remove(key);
            if (remove.longValue() == 0) {
                throw new DuplicateSubmitException(CommonExceptionEnum.SYSTEM_FAILURE, "请勿重复提交");
            }

        }
    }

}
