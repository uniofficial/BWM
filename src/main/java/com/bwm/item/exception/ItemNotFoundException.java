package com.bwm.item.exception;

import org.springframework.http.HttpStatus;

import com.bwm.global.exception.BusinessException;
import com.bwm.global.exception.ErrorCode;

public class ItemNotFoundException extends BusinessException {

    public ItemNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ItemNotFoundException(ErrorCode errorCode, String logMessage){
        super(errorCode, logMessage);
    }
}
