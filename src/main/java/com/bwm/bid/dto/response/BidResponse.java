package com.bwm.bid.dto.response;

import java.time.LocalDateTime;

import com.bwm.bid.entity.Bid;

/**
 * 입찰 등록 응답 DTO 
 *
 * 사용자 식별 정보 보호를 위해
 * bidderId 대신 화면 표시용 닉네임만 반환 
 */
public record BidResponse(
        Integer bidId,
        Integer itemId,
        String bidderNickname,
        Integer bidAmount,
        LocalDateTime bidAt
) {

    public static BidResponse from(Bid bid) {
        return new BidResponse(
                bid.getBidId(),
                bid.getItem().getItemId(),
                bid.getBidder().getNickname(),
                bid.getBidAmount(),
                bid.getBidAt()
        );
    }
}