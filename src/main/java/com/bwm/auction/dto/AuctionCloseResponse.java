package com.bwm.auction.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 경매 종료 응답 dto 
 *
 * 경매 끝나도 아래와 같이 동일한 필드명 사용 
 * highestBidder -> 낙찰자 
 * currentPrice -> 최종 낙찰가
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuctionCloseResponse {

    private Long itemId; // 종료된 상품 아이디 
    
    private String status; // 종료 후 상품 상태 (SOLD / UNSOLD)

    /**
     * 최고 입찰자 아이디
     * 
     * 낙찰된 경우 최종 낙찰자의 사용자 아이디, 
     * 유찰된 경우 null 
     */
    private Long highestBidderId;

    /**
     * 현재 가격
     *
     * 낙찰된 경우 최종 낙찰가, 
     * 유찰된 경우 상품의 시작가 상태로 남음 
     */
    private Integer currentPrice; 
}