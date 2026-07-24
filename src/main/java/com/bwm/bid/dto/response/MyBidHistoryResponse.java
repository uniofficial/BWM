package com.bwm.bid.dto.response;

import java.time.LocalDateTime;
import java.util.Objects;

import com.bwm.bid.entity.Bid;
import com.bwm.item.entity.Item;
import com.bwm.item.entity.ItemStatus;

/**
 * 내 입찰 내역 조회 응답 DTO
 *
 * 사용자가 입찰한 상품과 입찰 정보를 반환
 * 사용자 ID와 같은 내부 식별 정보는 응답에 포함하지 않음 
 */
public record MyBidHistoryResponse(

        /**
         * 입찰 ID
         */
        Integer bidId,

        /**
         * 입찰한 상품 ID
         */
        Integer itemId,

        /**
         * 입찰한 상품명
         */
        String itemTitle,

        /**
         * 입찰한 상품의 대표 이미지 URL. 등록된 이미지가 없으면 null
         */
        String representativeImageUrl,

        /**
         * 사용자가 입찰한 금액
         */
        Integer bidAmount,

        /**
         * 입찰 시각
         */
        LocalDateTime bidAt,

        /**
         * 해당 상품의 현재 가격
         */
        Integer currentPrice,

        /**
         * 해당 상품의 현재 경매 상태
         */
        ItemStatus itemStatus,

        /**
         * 조회 사용자가 현재 최고 입찰자인지 여부
         */
        boolean highestBid

) {

    /**
     * Bid 엔티티를 내 입찰 내역 응답 DTO로 변환
     *
     * 현재 상품의 최고 입찰자와 조회 사용자를 비교하여
     * 최고 입찰 여부를 계산
     *
     * @param bid 입찰 엔티티
     * @param userId 조회 사용자 ID
     * @param representativeImageUrl 입찰한 상품의 대표 이미지 URL (없으면 null)
     * @return 내 입찰 내역 응답
     */
    public static MyBidHistoryResponse from(
            Bid bid,
            Integer userId,
            String representativeImageUrl
    ) {
        Item item = bid.getItem();

        boolean highestBid =
                item.getHighestBidder() != null
                && Objects.equals(
                        item.getHighestBidder().getUserId(),
                        userId
                );

        return new MyBidHistoryResponse(
                bid.getBidId(),
                item.getItemId(),
                item.getTitle(),
                representativeImageUrl,
                bid.getBidAmount(),
                bid.getBidAt(),
                item.getCurrentPrice(),
                item.getStatus(),
                highestBid
        );
    }
}