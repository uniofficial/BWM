package com.bwm.auction.exception;

import com.bwm.global.exception.BusinessException;
import com.bwm.global.exception.ErrorCode;

/**
 * 상품 판매자가 아닌 사용자가 경매 종료를 요청할 때 발생하는 예외
 */
public class AuctionPermissionDeniedException extends BusinessException {

    public AuctionPermissionDeniedException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AuctionPermissionDeniedException(
            ErrorCode errorCode,
            Integer itemId,
            Integer requesterId) {

        super(errorCode,
                "해당 경매를 종료할 권한이 없습니다. "
                        + "itemId=" + itemId
                        + ", requesterId=" + requesterId
        );
    }
}