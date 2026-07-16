

package com.bwm.item.entity;

/**
 * 상품(경매)의 진행 상태를 나타내는 enum.
 *
 * DB의 item.status 컬럼과 1:1로 매핑된다.
 * (DDL: `status` ENUM('OPEN','SOLD','UNSOLD','CANCELLED') NOT NULL DEFAULT 'OPEN')
 *
 * 상태 전이 흐름(참고용):
 *   OPEN --(마감시각 도달 + 낙찰자 있음)--> SOLD
 *   OPEN --(마감시각 도달 + 낙찰자 없음)--> UNSOLD
 *   OPEN --(판매자가 직접 취소)--------> CANCELLED
 *
 * 주의: Item 엔티티에서 @Enumerated(EnumType.STRING)으로 저장하므로,
 *       이 enum 상수 이름(OPEN/SOLD/UNSOLD/CANCELLED)을 절대 임의로 바꾸면 안 된다.
 *       바꾸면 DB에 이미 저장된 문자열과 매핑이 깨진다.
 */

public enum ItemStatus {

    // 경매가 진행 중인 상태. 상품 등록 직후 기본값. 입찰 가능 
    OPEN,

    // 마감 시각이 지났고, 낙찰자가 있어 거래 성사된 상태
    SOLD,

    // 마감 시각이 지났지만 입찰자가 없어 유찰된 상태
    UNSOLD,

    // 판매자가 경매 마감 전에 직접 취소한 상태(입찰이 이미 들어온 경우 취소 가능 여부는 서비스 로직에서 별도 검증
    CANCELLED
}
