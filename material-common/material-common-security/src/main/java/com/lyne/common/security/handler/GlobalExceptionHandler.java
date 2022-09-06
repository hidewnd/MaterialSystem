package com.lyne.common.security.handler;


import com.lyne.common.core.base.R;
import com.lyne.common.core.constant.HttpStatus;
import com.lyne.common.core.exception.InnerAuthException;
import com.lyne.common.core.exception.PreAuthorizeException;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理
 *
 * @author lyne
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 远程调用异常
     */
    @ExceptionHandler(FeignException.class)
    public R<?> feignException(FeignException e) {
        log.error("[Common 远程调用异常捕获]: {}", e.getMessage());
        return R.fail(e.getMessage());
    }

    /**
     * 权限认证异常
     */
    @ExceptionHandler(PreAuthorizeException.class)
    public R<?> handlePreAuthorizeException(PreAuthorizeException e, HttpServletRequest request) {
        log.warn("[Common 权限认证异常捕获]: 请求地址: '{}', 权限校验失败, 失败原因: {}", request.getRequestURI(), e.getMessage());
        return R.fail(HttpStatus.FORBIDDEN, "没有权限，请联系管理员授权");
    }

    /**
     * 内部认证异常
     */
    @ExceptionHandler(InnerAuthException.class)
    public R<?> handleInnerAuthException(InnerAuthException e, HttpServletRequest request) {
        log.info("[Common NotFound异常捕获]: 请求地址: '{}', 权限校验失败, 失败原因: {}", request.getRequestURI(), e.getMessage());
        return R.fail("内部服务结构, 无权调用");
    }


}
