package com.bwm.item.exception;

import org.springframework.http.HttpStatus;

import com.bwm.global.exception.BusinessException;
import com.bwm.global.exception.ErrorCode;

public class ItemStateConflictException extends BusinessException{

    public ItemStateConflictException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ItemStateConflictException(ErrorCode errorCode, String logMessage) {
        super(errorCode, logMessage);
    }
}
