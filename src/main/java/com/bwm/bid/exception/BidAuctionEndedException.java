package com.bwm.bid.exception;

import com.bwm.global.exception.BusinessException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * 마감 시간이 지난 경매에 입찰할 때 발생하는 예외
 */
public class BidAuctionEndedException extends BusinessException {

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

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.CONFLICT;
    }

    @Override
    public String getErrorCode() {
        return "AUCTION_ENDED";
    }
}