package com.bwm.bid.exception;

import com.bwm.global.exception.BusinessException;
import com.bwm.global.exception.ErrorCode;
import com.bwm.item.entity.ItemStatus;

/**
 * 진행 중이 아닌 경매에 입찰할 때 발생하는 예외
 */
public class BidNotOpenException extends BusinessException {

    public BidNotOpenException(ErrorCode errorCode) {
        super(errorCode);
    }

    public BidNotOpenException(ErrorCode errorCode, Integer itemId, ItemStatus status) {
        super(errorCode,
                "진행 중인 경매에만 입찰할 수 있습니다. "
                        + "itemId=" + itemId
                        + ", status=" + status
        );
    }
}