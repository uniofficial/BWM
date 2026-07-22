package com.bwm.auction.exception;

import com.bwm.global.exception.BusinessException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * 마감 시간이 지나지 않은 경매를 종료하려 할 때 발생하는 예외입니다.
 */
public class AuctionNotEndedException extends BusinessException {

    public AuctionNotEndedException(
            Integer itemId,
            LocalDateTime auctionEndAt) {

        super(
                "아직 경매 마감 시간이 되지 않았습니다. "
                        + "itemId=" + itemId
                        + ", auctionEndAt=" + auctionEndAt
        );
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.CONFLICT;
    }

    @Override
    public String getErrorCode() {
        return "AUCTION_NOT_ENDED";
    }
}