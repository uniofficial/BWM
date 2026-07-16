package com.bwm.bid.dto.response;

import java.time.LocalDateTime;

import com.bwm.bid.entity.Bid;

/**
 * 입찰 등록 및 입찰 내역 조회 응답 DTO
 */
public record BidResponse(
        Integer bidId,
        Integer itemId,
        Integer bidderId,
        String bidderNickname,
        Integer bidAmount,
        LocalDateTime bidAt
) {

    public static BidResponse from(Bid bid) {
        return new BidResponse(
                bid.getBidId(),
                bid.getItem().getItemId(),
                bid.getBidder().getUserId(),
                bid.getBidder().getNickname(),
                bid.getBidAmount(),
                bid.getBidAt()
        );
    }
}