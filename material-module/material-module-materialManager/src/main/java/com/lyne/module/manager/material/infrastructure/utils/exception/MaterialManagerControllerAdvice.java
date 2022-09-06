package com.lyne.module.manager.material.infrastructure.utils.exception;

import com.lyne.common.core.base.BaseException;
import com.lyne.common.core.base.R;
import com.lyne.common.core.constant.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;

/**
 * @author lyne
 */
@RestControllerAdvice
public class MaterialManagerControllerAdvice {
    private static final Logger log = LoggerFactory.getLogger(MaterialManagerControllerAdvice.class);

    /**
     * 全局异常捕捉处理
     */
    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    @Order(3)
    public R<?> errorHandler(Exception ex) {
        log.error("[MaterialManager 全局异常捕获] {}", ex.getMessage());
        return R.fail(ex.getMessage());
    }

    /**
     * 拦截捕捉自定义异常 BaseException.class
     */
    @ResponseBody
    @ExceptionHandler(value = BaseException.class)
    @Order(1)
    public R<?> myErrorHandler(BaseException ex) {
        log.warn("[MaterialManager 业务异常捕获] {}", ex.getMessage());
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