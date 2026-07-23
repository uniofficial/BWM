package com.bwm.item.exception;

import org.springframework.http.HttpStatus;

import com.bwm.global.exception.BusinessException;

public class ItemStateConflictException extends BusinessException{

    public ItemStateConflictException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.CONFLICT;
    }

    @Override
    public String getErrorCode() {
        return "ITEM_STATE_CONFLICT";
    }
}
