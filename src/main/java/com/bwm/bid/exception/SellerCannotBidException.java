package com.bwm.bid.exception;

import com.bwm.global.exception.BusinessException;
import com.bwm.global.exception.ErrorCode;

/**
 * 판매자가 자신의 상품에 입찰할 때 발생하는 예외
 */
public class SellerCannotBidException extends BusinessException {

    public SellerCannotBidException(ErrorCode errorCode) {
        super(errorCode);
    }

    public SellerCannotBidException(ErrorCode errorCode, Integer itemId, Integer sellerId) {
        super(errorCode,
                "판매자는 자신의 상품에 입찰할 수 없습니다. "
                        + "itemId=" + itemId
                        + ", sellerId=" + sellerId
        );
    }
}