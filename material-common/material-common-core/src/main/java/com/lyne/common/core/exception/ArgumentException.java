package com.lyne.common.core.exception;

import com.lyne.common.core.base.BaseException;

/**
 * 参数异常
 *
 * @author lyne
 */
public class ArgumentException extends BaseException {
    public ArgumentException() {
    }

    public ArgumentException(String message) {
        super(message);
    }

    public ArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
