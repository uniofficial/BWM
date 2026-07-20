package com.bwm.auction.dto;

import java.time.LocalDateTime;

import com.bwm.item.entity.Item;
import com.bwm.item.entity.ItemStatus;

/**
 * 로그인 사용자의 판매 완료 내역 조회 응답 DTO
 *
 * 판매 완료된 상품 정보와 구매자 정보를 반환합니다.
 */
public record SoldAuctionResponse(
        Integer itemId,
        String title,
        String category,
        String buyerNickname,
        Integer sellingPrice,
        LocalDateTime auctionEndAt,
        ItemStatus status
) {

    /**
     * SOLD 상태의 Item 엔티티를 판매 완료 내역 응답 DTO로 변환합니다.
     *
     * @param item 판매 완료된 상품
     * @return 판매 완료 내역 응답
     */
    public static SoldAuctionResponse from(Item item) {
        return new SoldAuctionResponse(
                item.getItemId(),
                item.getTitle(),
                item.getCategory(),
                item.getHighestBidder().getNickname(),
                item.getCurrentPrice(),
                item.getAuctionEndAt(),
                item.getStatus()
        );
    }
}