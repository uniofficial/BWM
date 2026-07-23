package com.bwm.bid.exception;

import com.bwm.global.exception.BusinessException;
import org.springframework.http.HttpStatus;

/**
 * 입찰 대상 상품을 찾을 수 없을 때 발생하는 예외
 */
public class BidItemNotFoundException extends BusinessException {

    public BidItemNotFoundException(Integer itemId) {
        super("입찰 대상 상품을 찾을 수 없습니다. itemId=" + itemId);
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