package com.bwm.bid.exception;

import com.bwm.global.exception.BusinessException;
import com.bwm.global.exception.ErrorCode;

import java.time.LocalDateTime;

/**
 * 마감 시간이 지난 경매에 입찰할 때 발생하는 예외
 */
public class BidAuctionEndedException extends BusinessException {

    public BidAuctionEndedException(ErrorCode errorCode) {
        super(errorCode);
    }

    public BidAuctionEndedException(
            ErrorCode errorCode,
            Integer itemId,
            LocalDateTime auctionEndAt
    ) {
        super(errorCode,
                "이미 마감된 경매에는 입찰할 수 없습니다. "
                        + "itemId=" + itemId
                        + ", auctionEndAt=" + auctionEndAt
        );
    }
}