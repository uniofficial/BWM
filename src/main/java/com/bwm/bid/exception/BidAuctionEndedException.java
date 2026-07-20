package com.bwm.bid.exception;

import java.time.LocalDateTime;

/**
 * 마감 시간이 지난 경매에 입찰할 때 발생하는 예외
 */
public class BidAuctionEndedException extends RuntimeException {

    public BidAuctionEndedException(
            Integer itemId,
            LocalDateTime auctionEndAt
    ) {
        super(
                "이미 마감된 경매에는 입찰할 수 없습니다. "
                + "itemId=" + itemId
                + ", auctionEndAt=" + auctionEndAt
        );
    }
}