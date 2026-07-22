package com.bwm.wallet.exception;

public class InvalidAmountException extends WalletException {
    public InvalidAmountException() {
        super("INVALID_AMOUNT", "유효하지 않은 포인트 금액입니다.");
    }

    public InvalidAmountException(String message) {
        super("INVALID_AMOUNT", message);
    }
}
