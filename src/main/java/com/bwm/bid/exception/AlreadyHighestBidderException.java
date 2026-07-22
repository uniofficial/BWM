package com.bwm.bid.exception;

import com.bwm.global.exception.BusinessException;
import org.springframework.http.HttpStatus;

/**
 * 현재 최고 입찰자가 같은 상품에 다시 입찰할 때 발생하는 예외
 */
public class AlreadyHighestBidderException extends BusinessException {

    public AlreadyHighestBidderException(
            Integer itemId,
            Integer bidderId
    ) {
        super(
                "현재 최고 입찰자는 다시 입찰할 수 없습니다. "
                        + "itemId=" + itemId
                        + ", bidderId=" + bidderId
        );
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.CONFLICT;
    }

    @Override
    public String getErrorCode() {
        return "ALREADY_HIGHEST_BIDDER";
    }
}