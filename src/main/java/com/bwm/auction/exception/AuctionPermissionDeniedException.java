package com.bwm.auction.exception;

/**
 * 상품 판매자가 아닌 사용자가 경매 종료를 요청할 때 발생하는 예외
 */
public class AuctionPermissionDeniedException extends RuntimeException {

    public AuctionPermissionDeniedException(
            Integer itemId,
            Integer requesterId) {

        super(
                "해당 경매를 종료할 권한이 없습니다. "
                + "itemId=" + itemId
                + ", requesterId=" + requesterId
        );
    }
}