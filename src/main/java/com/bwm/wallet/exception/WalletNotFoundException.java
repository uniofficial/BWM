package com.bwm.wallet.exception;

import com.bwm.global.exception.BusinessException;
import com.bwm.global.exception.ErrorCode;

public class WalletNotFoundException extends BusinessException {
    public WalletNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

    public WalletNotFoundException(ErrorCode errorCode, String logMessage) {
        super(errorCode, logMessage);
    }
}
