package com.lyne.auth.exception;

import com.lyne.common.core.base.BaseException;

/**
 * 授权服务业务异常
 * @author lyne
 */
public class AuthServerException extends BaseException {
    public AuthServerException() {
    }

    public AuthServerException(String message) {
        super(message);
    }
}
