package com.lyne.module.manager.material.infrastructure.utils.exception;

import com.lyne.common.core.base.BaseException;

/**
 * 状态检查异常
 * @author lyne
 */
public class StateCheckException extends BaseException {
    public StateCheckException() {
        super();
    }

    public StateCheckException(String message) {
        super(message);
    }
}
