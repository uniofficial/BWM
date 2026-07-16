package com.bwm.auction.exception;

import com.bwm.item.entity.ItemStatus;

/**
 * 이미 종료되거나 취소된 경매를 다시 종료하려 할 때 발생하는 예외입니다.
 */
public class AuctionAlreadyClosedException extends RuntimeException {

    public AuctionAlreadyClosedException(
            Integer itemId,
            ItemStatus currentStatus) {

        super(
                "이미 종료되었거나 진행 중이 아닌 경매입니다. "
                + "itemId=" + itemId
                + ", currentStatus=" + currentStatus
        );
    }
}