package com.bwm.wallet.exception;

import com.bwm.global.exception.BusinessException;
import com.bwm.global.exception.ErrorCode;

public class InvalidAmountException extends BusinessException {
    public InvalidAmountException(ErrorCode errorCode) {
        super(errorCode);
    }

    public InvalidAmountException(ErrorCode errorCode, String logMessage) {
        super(errorCode, logMessage);
    }
}
