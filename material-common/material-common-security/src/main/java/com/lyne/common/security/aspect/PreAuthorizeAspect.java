package com.lyne.common.security.aspect;

import com.lyne.common.cache.redis.service.RedisService;
import com.lyne.common.core.exception.PreAuthorizeException;
import com.lyne.common.security.annotation.PreAuthorize;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import javax.annotation.Resource;

/**
 * 自定义权限实现
 *
 * @author lyne
 */
@Aspect
@Component
public class PreAuthorizeAspect {

    /**
     * 所有权限标识
     */
    private static final String ALL_PERMISSION = "*:*:*";

    @Resource
    private RedisService redisService;

    @Around("@annotation(preAuthorize)")
    public Object around(ProceedingJoinPoint point, PreAuthorize preAuthorize) throws Throwable {
        Signature signature = point.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        PreAuthorize annotation = method.getAnnotation(PreAuthorize.class);
        if (annotation == null) {
            return point.proceed();
        }
        if (annotation.hasPerm().length() != 0) {
            if (hasPerm(annotation.hasPerm())) {
                return point.proceed();
            }
            throw new PreAuthorizeException();
        }
        return point.proceed();
    }

    /**
     * 验证用户是否具备某权限
     *
     * @param permission 权限字符串
     * @return 用户是否具备某权限
     */
    private boolean hasPerm(String permission) {
        return false;
    }


}
