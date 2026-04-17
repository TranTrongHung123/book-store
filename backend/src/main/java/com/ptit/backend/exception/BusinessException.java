package com.ptit.backend.exception;

public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.name());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public enum ErrorCode {
        ORDER_NOT_FOUND,
        INVALID_AMOUNT,
        ORDER_ALREADY_CONFIRMED
    }
}