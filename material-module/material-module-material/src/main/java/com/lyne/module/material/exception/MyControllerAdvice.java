package com.lyne.module.material.exception;

import com.lyne.common.core.base.BaseException;
import com.lyne.common.core.base.R;
import com.lyne.common.core.constant.HttpStatus;
import com.lyne.common.security.handler.GlobalExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * @author lyne
 */
@RestControllerAdvice
public class MyControllerAdvice {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 全局异常捕捉处理
     */
    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    @Order(3)
    public R<?> errorHandler(Exception ex) {
        log.error("[Material 服务全局异常捕获]: {}", ex.getMessage());
        return R.fail(ex.getMessage());
    }

    /**
     * 拦截捕捉自定义异常 BaseException.class
     */
    @ResponseBody
    @ExceptionHandler(value = BaseException.class)
    @Order(1)
    public R<?> myErrorHandler(BaseException ex) {
        log.warn("[Material 服务业务异常捕获]: {}", ex.getMessage());
        return R.fail(ex.getMessage());
    }

    /**
     * 拦截捕捉404异常 BaseException.class
     */
    @ResponseBody
    @ExceptionHandler(value = NoHandlerFoundException.class)
    @Order(2)
    public R<?> myErrorHandler(NoHandlerFoundException ex) {
        return R.fail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

}
