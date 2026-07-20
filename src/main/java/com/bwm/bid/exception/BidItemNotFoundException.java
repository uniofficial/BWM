package com.bwm.bid.exception;

/**
 * 입찰 대상 상품을 찾을 수 없을 때 발생하는 예외
 */
public class BidItemNotFoundException extends RuntimeException {

    public BidItemNotFoundException(Integer itemId) {
        super("입찰 대상 상품을 찾을 수 없습니다. itemId=" + itemId);
    }
}