package com.bwm.item.dto.response;

import java.time.LocalDateTime;

import com.bwm.item.entity.Item;
import com.bwm.item.entity.ItemStatus;

/**
 * GET /api/items (상품 목록 조회) 응답 바디의 원소 하나.
 * 목록 화면에서는 상세 정보 전부가 필요 없으므로, ItemResponse보다 가벼운 필드만 담는다.
 */


public record ItemSummaryResponse(
    Integer itemId,
    String title,
    String category,
    Integer currentPrice,
    LocalDateTime auctionEndAt,
    ItemStatus status,
    LocalDateTime createdAt
) {
    public static ItemSummaryResponse from(Item item) {
        return new ItemSummaryResponse(item.getItemId(),
                                       item.getTitle(),
                                       item.getCategory(),
                                       item.getCurrentPrice(),
                                       item.getAuctionEndAt(),
                                       item.getStatus(),
                                       item.getCreatedAt()
        );
    }
    
}
