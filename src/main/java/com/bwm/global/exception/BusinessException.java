package com.bwm.global.exception;

import org.springframework.http.HttpStatus;

public abstract class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String logMessage;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.logMessage = errorCode.getMessage();
    }

    public BusinessException(ErrorCode errorCode, String logMessage) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.logMessage = logMessage;
    }

    public HttpStatus getStatus() {
        return errorCode.getStatus();
    }

    public String getErrorCode() {
        return errorCode.getCode();
    }

    public String getLogMessage() {
        return logMessage;
    }
}
