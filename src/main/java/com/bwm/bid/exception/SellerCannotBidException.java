package com.bwm.bid.exception;

/**
 * 판매자가 자신의 상품에 입찰할 때 발생하는 예외
 */
public class SellerCannotBidException extends RuntimeException {

    public SellerCannotBidException(Integer itemId, Integer sellerId) {
        super(
                "판매자는 자신의 상품에 입찰할 수 없습니다. "
                + "itemId=" + itemId
                + ", sellerId=" + sellerId
        );
    }
}