package com.bwm.item.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.bwm.item.entity.Item;
import com.bwm.item.entity.ItemStatus;

/**
 * GET /api/items/{itemId} (상품 상세 조회) 응답 바디.
 *
 * 목록(ItemSummaryResponse)과 달리 상세 화면에 필요한 정보를 전부 담는다:
 * 시작가, 설명, 이미지 목록, 최고 입찰자 닉네임까지 포함.
 * ItemResponse와 동일하게 사용자 식별 정보 보호를 위해 id 대신 닉네임만 노출한다.
 */
public record ItemDetailResponse(
    Integer itemId,
    String sellerNickname,       // User 엔티티 전체가 아니라 닉네임만 노출 (id/email/password 등 보호)
    String title,
    String category,
    Integer startPrice,          // 목록 DTO엔 없던 필드. 상세에서는 시작가도 같이 보여줌
    Integer currentPrice,
    LocalDateTime auctionEndAt,
    ItemStatus status,
    LocalDateTime createdAt,
    String description,          // 목록 DTO엔 없던 필드. 상세에서만 필요한 긴 텍스트라 여기만 포함
    String highestBidderNickname, // 아직 입찰이 없으면 null (Item.highestBidder가 null이므로)
    List<String> imageUrls        // 이 상품에 등록된 이미지들의 접근 URL 목록
) {
    /**
     * @param item 조회된 Item 엔티티
     * @param imageUrls 이 상품에 등록된 이미지 URL 목록 (대표 이미지가 0번째)
     */
    public static ItemDetailResponse from(Item item, List<String> imageUrls) {
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
            // 최고 입찰자가 없는 상품(아직 입찰 0건)은 highestBidder가 null이므로,
            // 그대로 getNickname()을 호출하면 NPE가 나서 null 체크 후 분기 처리
            item.getHighestBidder() != null ? item.getHighestBidder().getNickname() : null,
            imageUrls
        );
    }
}