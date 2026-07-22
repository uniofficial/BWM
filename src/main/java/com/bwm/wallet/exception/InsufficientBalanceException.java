package com.bwm.wallet.exception;

public class InsufficientBalanceException extends WalletException {
    public InsufficientBalanceException() {
        super("INSUFFICIENT_BALANCE", "보유 포인트 잔액이 부족합니다.");
    }

    public InsufficientBalanceException(String message) {
        super("INSUFFICIENT_BALANCE", message);
    }
}
