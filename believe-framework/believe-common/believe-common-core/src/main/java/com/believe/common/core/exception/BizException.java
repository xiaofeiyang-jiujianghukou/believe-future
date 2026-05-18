package com.believe.common.core.exception;

import com.believe.common.core.constants.ErrorCode;

public class BizException extends BelieveException {

    public BizException(String message) {
        super(ErrorCode.BIZ_GENERAL, message);
    }

    public BizException(int code, String message) {
        super(code, message);
    }
}
