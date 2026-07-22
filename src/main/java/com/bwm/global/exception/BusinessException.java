package com.bwm.global.exception;

import org.springframework.http.HttpStatus;

public abstract class BusinessException extends RuntimeException {

    protected BusinessException(String message) {
        super(message);
    }

    public abstract HttpStatus getStatus();
    public abstract String getErrorCode();
}
