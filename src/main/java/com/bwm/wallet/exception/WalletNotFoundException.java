package com.bwm.wallet.exception;

public class WalletNotFoundException extends WalletException {
    public WalletNotFoundException() {
        super("WALLET_NOT_FOUND", "해당 유저의 지갑을 찾을 수 없습니다.");
    }

    public WalletNotFoundException(String message) {
        super("WALLET_NOT_FOUND", message);
    }
}
