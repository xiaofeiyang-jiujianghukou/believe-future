package com.believe.common.core.exception;

import lombok.Getter;

@Getter
public class BelieveException extends RuntimeException {

    private final int code;

    public BelieveException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BelieveException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

}
