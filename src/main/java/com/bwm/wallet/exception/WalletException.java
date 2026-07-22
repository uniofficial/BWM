package com.bwm.wallet.exception;

public class WalletException extends RuntimeException {
    private final String errorCode;

    public WalletException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
