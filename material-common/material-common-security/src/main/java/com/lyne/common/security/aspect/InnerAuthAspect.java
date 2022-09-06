package com.lyne.common.security.aspect;

import com.lyne.common.core.constant.SecurityConstants;
import com.lyne.common.core.exception.InnerAuthException;
import com.lyne.common.core.utils.servlet.ServletUtils;
import com.lyne.common.security.annotation.InnerAuth;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 内部服务调用验证处理
 *
 * @author lyne
 */

@Aspect
@Component
public class InnerAuthAspect implements Ordered {

    @Around("@annotation(innerAuth)")
    public Object innerAround(ProceedingJoinPoint point, InnerAuth innerAuth) throws Throwable {
        HttpServletRequest request = ServletUtils.getRequest();
        if (request != null) {
            String source = request.getHeader(SecurityConstants.FROM_SOURCE);
            // 内部请求验证
            if (!SecurityConstants.INNER.equals(source)) {
                throw new InnerAuthException("没有内部访问权限，不允许访问");
            }
            if (innerAuth.isUser()) {
                String userid = ServletUtils.getRequest().getHeader(SecurityConstants.TOKEN_USER_ID);
                String username = ServletUtils.getRequest().getHeader(SecurityConstants.DETAILS_USERNAME);
                // 用户信息验证
                if (userid.isEmpty() || username.isEmpty()) {
                    throw new InnerAuthException("没有设置用户信息，不允许访问 ");
                }
            }
        }
        return point.proceed();
    }


    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}
