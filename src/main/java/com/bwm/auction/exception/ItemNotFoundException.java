package com.bwm.auction.exception;

import com.bwm.global.exception.BusinessException;
import org.springframework.http.HttpStatus;

/**
 * 요청한 상품이 존재하지 않을 때 발생하는 예외
 */
public class ItemNotFoundException extends BusinessException {

    public ItemNotFoundException(Integer itemId) {
        super("상품을 찾을 수 없습니다. itemId=" + itemId);
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