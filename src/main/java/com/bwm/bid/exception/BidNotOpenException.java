package com.bwm.bid.exception;

import com.bwm.item.entity.ItemStatus;

/**
 * 진행 중이 아닌 경매에 입찰할 때 발생하는 예외
 */
public class BidNotOpenException extends RuntimeException {

    public BidNotOpenException(Integer itemId, ItemStatus status) {
        super(
                "진행 중인 경매에만 입찰할 수 있습니다. "
                + "itemId=" + itemId
                + ", status=" + status
        );
    }
}