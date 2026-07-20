package com.bwm.auction.exception;

/**
 * 요청한 상품이 존재하지 않을 때 발생하는 예외
 */
public class ItemNotFoundException extends RuntimeException {

    public ItemNotFoundException(Integer itemId) {
        super("상품을 찾을 수 없습니다. itemId=" + itemId);
    }
}