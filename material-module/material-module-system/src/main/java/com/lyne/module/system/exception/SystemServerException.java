package com.lyne.module.system.exception;

import com.lyne.common.core.base.BaseException;

/**
 * 系统服务异常
 *
 * @author lyne
 */
public class SystemServerException extends BaseException {
    public SystemServerException() {
    }

    public SystemServerException(String message) {
        super(message);
    }

    public SystemServerException(String message, Throwable cause) {
        super(message, cause);
    }
}
