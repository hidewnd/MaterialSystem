package com.lyne.module.system.controller;

import com.lyne.common.core.base.BaseException;
import com.lyne.common.core.base.R;
import com.lyne.common.core.constant.HttpStatus;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理
 * @author lyne
 */
@Slf4j
@ControllerAdvice
public class MyControllerAdvice {
    /**
     * 全局异常捕捉处理
     */
    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    @Order(3)
    public R<?> errorHandler(Exception ex) {
        log.error("[System 全局异常捕获] {}", ex.getMessage());
        return R.fail(ex.getMessage());
    }

    /**
     * 拦截捕捉自定义异常 BaseException.class
     */
    @ResponseBody
    @ExceptionHandler(value = BaseException.class)
    @Order(1)
    public R<?> myErrorHandler(BaseException ex) {
        log.warn("[System 业务异常捕获] {}", ex.getMessage());
        return R.fail(HttpStatus.BUSINESS_ERROR, ex.getMessage());
    }

    /**
     * 拦截捕捉404异常 BaseException.class
     */
    @ResponseBody
    @ExceptionHandler(value = NoHandlerFoundException.class)
    @Order(2)
    public R<?> myErrorHandler(NoHandlerFoundException ex, HttpServletRequest request) {
        log.info("[MaterialManager NotFound异常捕获] 请求路径:{}, 异常原因:{}", request.getRequestURI(), ex.getMessage());
        return R.fail(HttpStatus.NOT_FOUND, "404 Not found");
    }
}
