package com.bwm.item.dto.response;

import java.time.LocalDateTime;

import com.bwm.item.entity.Item;
import com.bwm.item.entity.ItemStatus;

/**
 * 상품 등록 및 조회 응답 DTO
 *
 * Item 엔티티를 그대로 반환하지 않고 DTO로 변환하여
 * 엔티티 구조 변경이 API 응답에 직접 영향을 주지 않도록 함 
 *
 * 사용자 식별 정보 보호를 위해
 * 판매자 ID 대신 화면 표시용 닉네임만 반환 
 */
public record ItemResponse(
        Integer itemId,
        String sellerNickname,
        String title,
        String category,
        Integer startPrice,
        Integer currentPrice,
        LocalDateTime auctionEndAt,
        ItemStatus status,
        LocalDateTime createdAt,
        String description
) {

    /**
     * Item 엔티티를 ItemResponse로 변환 
     *
     * @param item 변환할 Item 엔티티
     * @return 상품 응답 DTO
     */
    public static ItemResponse from(Item item) {
        return new ItemResponse(
                item.getItemId(),
                item.getSeller().getNickname(),
                item.getTitle(),
                item.getCategory(),
                item.getStartPrice(),
                item.getCurrentPrice(),
                item.getAuctionEndAt(),
                item.getStatus(),
                item.getCreatedAt(),
                item.getDescription()
        );
    }
}
