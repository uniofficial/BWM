package com.bwm.auction.exception;

import com.bwm.global.exception.BusinessException;
import org.springframework.http.HttpStatus;

/**
 * 상품 판매자가 아닌 사용자가 경매 종료를 요청할 때 발생하는 예외
 */
public class AuctionPermissionDeniedException extends BusinessException {

    public AuctionPermissionDeniedException(
            Integer itemId,
            Integer requesterId) {

        super(
                "해당 경매를 종료할 권한이 없습니다. "
                        + "itemId=" + itemId
                        + ", requesterId=" + requesterId
        );
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.FORBIDDEN;
    }

    @Override
    public String getErrorCode() {
        return "AUCTION_PERMISSION_DENIED";
    }
}