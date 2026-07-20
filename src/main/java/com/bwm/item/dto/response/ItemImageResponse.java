package com.bwm.item.dto.response;

import java.time.LocalDateTime;

import com.bwm.item.entity.ItemImage;

/**
 * POST /api/items/{itemId}/images 응답 바디의 원소 하나.
 *
 * ItemImage 엔티티를 그대로 반환하지 않고 이 DTO로 변환해서 내려준다.
 * (ItemResponse와 동일한 이유: 연관관계 구조가 바뀌어도 API 스펙이 안 깨지게, 엔티티 그래프가 그대로 노출되지 않게)
 */

public record ItemImageResponse(
    Integer imageId,
    Integer itemId,
    String imageUrl,
    Boolean isRepresentative,
    LocalDateTime createdAt
) {
    /**
     * ItemImage 엔티티 -> ItemImageResponse 변환.
     *
     * @param itemImage 저장이 끝난(imageId가 채워진) ItemImage 엔티티
     */

    public static ItemImageResponse from(ItemImage itemImage){
        return new ItemImageResponse(
            itemImage.getImageId(),
            itemImage.getItem().getItemId(),
            itemImage.getImageUrl(),
            itemImage.getIsRepresentative(),
            itemImage.getCreatedAt()
        );
    }
} 
