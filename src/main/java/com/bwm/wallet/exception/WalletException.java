package com.bwm.wallet.exception;

import com.bwm.global.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class WalletException extends BusinessException {

    private final String errorCode;

    public WalletException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String getErrorCode() {
        return errorCode;
    }
}