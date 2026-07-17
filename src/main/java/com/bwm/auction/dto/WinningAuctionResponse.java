package com.bwm.auction.dto;

import java.time.LocalDateTime;

import com.bwm.item.entity.Item;
import com.bwm.item.entity.ItemStatus;

/**
 * 로그인 사용자의 낙찰 내역 조회 응답 DTO
 *
 * 낙찰 완료된 상품의 기본 정보와 판매자, 낙찰가를 반환함 
 */
public record WinningAuctionResponse(
        Integer itemId,
        String title,
        String category,
        String sellerNickname,
        Integer winningPrice,
        LocalDateTime auctionEndAt,
        ItemStatus status
) {

    /**
     * SOLD 상태의 Item 엔티티를 낙찰 내역 응답 DTO로 변환
     *
     * @param item 낙찰 완료된 상품
     * @return 낙찰 내역 응답
     */
    public static WinningAuctionResponse from(Item item) {
        return new WinningAuctionResponse(
                item.getItemId(),
                item.getTitle(),
                item.getCategory(),
                item.getSeller().getNickname(),
                item.getCurrentPrice(),
                item.getAuctionEndAt(),
                item.getStatus()
        );
    }
}