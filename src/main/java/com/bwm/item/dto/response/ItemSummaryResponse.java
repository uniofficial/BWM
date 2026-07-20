package com.bwm.item.dto.response;

import java.time.Duration;
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
    String highestBidderNickname, // 아직 입찰이 없으면 null
    LocalDateTime auctionEndAt,
    Long remainingSeconds,        // 마감까지 남은 시간(초). 이미 지났으면 0으로 고정
    ItemStatus status,
    LocalDateTime createdAt
) {
    public static ItemSummaryResponse from(Item item) {
        long remaining = Duration.between(LocalDateTime.now(), item.getAuctionEndAt()).getSeconds();

        return new ItemSummaryResponse(
            item.getItemId(),
            item.getTitle(),
            item.getCategory(),
            item.getCurrentPrice(),
            item.getHighestBidder() != null ? item.getHighestBidder().getNickname() : null,
            item.getAuctionEndAt(),
            Math.max(remaining, 0), // 음수(이미 마감)면 0으로 표시
            item.getStatus(),
            item.getCreatedAt()
        );
    }
}
