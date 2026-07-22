package com.bwm.item.exception;

import org.springframework.http.HttpStatus;

import com.bwm.global.exception.BusinessException;

public class ItemAccessDeniedException extends BusinessException {

    public ItemAccessDeniedException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.FORBIDDEN;
    }

    @Override
    public String getErrorCode() {
        return "ITEM_ACCESS_DENIED";
    }
}
