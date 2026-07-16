package com.bwm.auction.exception;

import java.time.LocalDateTime;

/**
 * 마감 시간이 지나지 않은 경매를 종료하려 할 때 발생하는 예외입니다.
 */
public class AuctionNotEndedException extends RuntimeException {

    public AuctionNotEndedException(
            Integer itemId,
            LocalDateTime auctionEndAt) {

        super(
                "아직 경매 마감 시간이 되지 않았습니다. "
                + "itemId=" + itemId
                + ", auctionEndAt=" + auctionEndAt
        );
    }
}