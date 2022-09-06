package com.lyne.module.manager.material.infrastructure.utils.exception;

import com.lyne.common.core.base.BaseException;

/**
 * 容量超出限制异常
 * @author lyne
 */
public class CapacityOutOfBoundException extends BaseException {

    public CapacityOutOfBoundException() {
    }

    public CapacityOutOfBoundException(String msg) {
        super(msg);
    }
}
