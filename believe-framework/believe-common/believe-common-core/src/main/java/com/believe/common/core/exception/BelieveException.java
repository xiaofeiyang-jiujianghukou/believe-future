package com.believe.common.core.exception;

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

    public int getCode() { return code; }
}
