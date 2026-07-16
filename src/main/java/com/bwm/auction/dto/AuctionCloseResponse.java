package com.bwm.auction.dto;

import com.bwm.item.entity.Item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 경매 종료 결과 응답 DTO입니다.
 *
 * 경매 종료 이후에도 다음 필드를 낙찰 정보로 사용합니다.
 *
 * highestBidder: 최종 낙찰자
 * currentPrice: 최종 낙찰가
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuctionCloseResponse {

    /**
     * 종료된 상품 ID
     */
    private Integer itemId;

    /**
     * 종료 후 상품 상태
     *
     * SOLD 또는 UNSOLD
     */
    private String status;

    /**
     * 최종 최고 입찰자의 사용자 ID
     *
     * 낙찰된 경우 최종 낙찰자 ID이며,
     * 유찰된 경우 null입니다.
     */
    private Integer highestBidderId;

    /**
     * 상품의 현재 가격
     *
     * 낙찰된 경우 최종 낙찰가이며,
     * 유찰된 경우 시작가 상태로 남습니다.
     */
    private Integer currentPrice;

    /**
     * Item 엔티티를 경매 종료 응답 DTO로 변환합니다.
     *
     * @param item 종료된 상품
     * @return 경매 종료 응답
     */
    public static AuctionCloseResponse from(Item item) {

        Integer highestBidderId =
                item.getHighestBidder() == null
                        ? null
                        : item.getHighestBidder().getUserId();

        return AuctionCloseResponse.builder()
                .itemId(item.getItemId())
                .status(item.getStatus().name())
                .highestBidderId(highestBidderId)
                .currentPrice(item.getCurrentPrice())
                .build();
    }
}