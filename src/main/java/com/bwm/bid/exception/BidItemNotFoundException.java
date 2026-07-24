package com.bwm.bid.exception;

import com.bwm.global.exception.BusinessException;
import com.bwm.global.exception.ErrorCode;

/**
 * 입찰 대상 상품을 찾을 수 없을 때 발생하는 예외
 */
public class BidItemNotFoundException extends BusinessException {

    public BidItemNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

    public BidItemNotFoundException(ErrorCode errorCode, Integer itemId) {
        super(errorCode, "입찰 대상 상품을 찾을 수 없습니다. itemId=" + itemId);
    }
}