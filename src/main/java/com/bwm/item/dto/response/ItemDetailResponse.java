package com.bwm.item.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.bwm.bid.dto.response.ItemBidHistoryResponse;
import com.bwm.item.entity.Item;
import com.bwm.item.entity.ItemStatus;

/**
 * GET /api/items/{itemId} (상품 상세 조회) 응답 바디.
 */
public record ItemDetailResponse(
    Integer itemId,
    String sellerNickname,
    String title,
    String category,
    Integer startPrice,
    Integer currentPrice,
    LocalDateTime auctionEndAt,
    ItemStatus status,
    LocalDateTime createdAt,
    String description,
    String highestBidderNickname,
    List<String> imageUrls,
    List<ItemBidHistoryResponse> bidHistory // 이 상품에 들어온 입찰 내역 (최신순)
) {
    public static ItemDetailResponse from(Item item, List<String> imageUrls, List<ItemBidHistoryResponse> bidHistory) {
        return new ItemDetailResponse(
            item.getItemId(),
            item.getSeller().getNickname(),
            item.getTitle(),
            item.getCategory(),
            item.getStartPrice(),
            item.getCurrentPrice(),
            item.getAuctionEndAt(),
            item.getStatus(),
            item.getCreatedAt(),
            item.getDescription(),
            item.getHighestBidder() != null ? item.getHighestBidder().getNickname() : null,
            imageUrls,
            bidHistory
        );
    }
}
