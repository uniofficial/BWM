package com.bwm.item.exception;

import org.springframework.http.HttpStatus;

import com.bwm.global.exception.BusinessException;

public class ItemNotFoundException extends BusinessException {

    public ItemNotFoundException(String message){
        super(message);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.NOT_FOUND;
    }

    @Override
    public String getErrorCode() {
        return "ITEM_NOT_FOUND";
    }
}
