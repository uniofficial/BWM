package com.bwm.item.exception;

import org.springframework.http.HttpStatus;

import com.bwm.global.exception.BusinessException;
import com.bwm.global.exception.ErrorCode;

public class ItemAccessDeniedException extends BusinessException {

    public ItemAccessDeniedException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ItemAccessDeniedException(ErrorCode errorCode, String logMessage) {
        super(errorCode, logMessage);
    }
}
