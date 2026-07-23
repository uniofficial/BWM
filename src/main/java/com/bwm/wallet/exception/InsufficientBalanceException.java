package com.bwm.wallet.exception;

import com.bwm.global.exception.BusinessException;
import com.bwm.global.exception.ErrorCode;

public class InsufficientBalanceException extends BusinessException {
    public InsufficientBalanceException(ErrorCode errorCode) {
        super(errorCode);
    }

    public InsufficientBalanceException(ErrorCode errorCode, String logMessage) {
        super(errorCode, logMessage);
    }
}
