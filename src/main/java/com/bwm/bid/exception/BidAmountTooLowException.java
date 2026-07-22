package com.bwm.bid.exception;

import com.bwm.global.exception.BusinessException;
import org.springframework.http.HttpStatus;

/**
 * 입찰 금액이 최소 입찰 가능 금액보다 낮을 때 발생하는 예외
 */
public class BidAmountTooLowException extends BusinessException {

    public BidAmountTooLowException(
            Integer currentPrice,
            Integer bidAmount,
            Integer minimumBidAmount
    ) {
        super(
                "입찰 금액은 현재가보다 최소 100P 이상 높아야 합니다. "
                        + "currentPrice=" + currentPrice
                        + ", minimumBidAmount=" + minimumBidAmount
                        + ", bidAmount=" + bidAmount
        );
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String getErrorCode() {
        return "BID_AMOUNT_TOO_LOW";
    }
}