package com.bwm.item.dto.response;

import java.time.LocalDateTime;

import com.bwm.item.entity.Item;
import com.bwm.item.entity.ItemStatus;

/**
 * POST /api/items 응답 바디.
 *
 * Item 엔티티를 그대로 반환하지 않고 이 DTO로 변환해서 내려주는 이유:
 * 1) 엔티티 필드가 바뀌어도(예: seller 연관관계 구조 변경) API 응답 스펙이 자동으로 안 깨지게 하려고
 * 2) User 엔티티 전체(비밀번호 등 민감정보 포함)가 JSON 직렬화 과정에서 실수로 노출되는 걸 원천 차단하려고
 *    (sellerId 하나만 Long으로 꺼내서 내려줌, User 객체 자체를 넘기지 않음)
 */


public record ItemResponse(
    Integer itemId,
    Integer sellerId,
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
     * Item 엔티티 -> ItemResponse 변환.
     * Service 계층에서 itemRepository.save(item) 결과를 이 메서드에 넘겨 응답을 만든다.
     *
     * @param item 저장이 끝난(itemId가 채워진) Item 엔티티
     */

    public static ItemResponse from(Item item) {
        return new ItemResponse(
            item.getItemId(),
            item.getSeller().getUserId(),
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

