package com.bwm.bid.dto.response;

import java.time.LocalDateTime;

import com.bwm.bid.entity.Bid;

/**
 * 상품별 입찰 내역 조회 응답 DTO
 *
 * 사용자 개인정보 보호를 위해
 * 사용자 ID나 이메일은 반환하지 않고 닉네임만 제공!! 
 */
public record ItemBidHistoryResponse(
        Integer bidId,
        String bidderNickname,
        Integer bidAmount,
        LocalDateTime bidAt
) {

    public static ItemBidHistoryResponse from(Bid bid) {
        return new ItemBidHistoryResponse(
                bid.getBidId(),
                bid.getBidder().getNickname(),
                bid.getBidAmount(),
                bid.getBidAt()
        );
    }
}