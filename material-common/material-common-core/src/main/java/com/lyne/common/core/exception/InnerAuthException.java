package com.lyne.common.core.exception;

import com.lyne.common.core.base.BaseException;

/**
 * @author lyne
 */
public class InnerAuthException extends BaseException {
    private static final long serialVersionUID = 1L;

    public InnerAuthException(String message) {
        super(message);
    }
}
