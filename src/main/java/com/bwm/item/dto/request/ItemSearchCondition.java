package com.bwm.item.dto.request;

/**
 * GET /api/items/search 요청 조건.
 * 모든 필드는 선택사항(null 허용) — null인 조건은 검색 시 그냥 무시되고, 넣은 조건들끼리는 AND로 묶인다.
 * 쿼리 파라미터로 그대로 바인딩됨 (예: ?keyword=노트북&category=전자기기&minPrice=10000)
 */
public record ItemSearchCondition(
    String keyword,      // 상품명(title)에 포함된 검색어. null/빈 문자열이면 제목 조건 없음
    String category,     // 정확히 일치하는 카테고리. null이면 카테고리 조건 없음
    String status,       // 상품 상태 문자열. OPEN/SOLD/UNSOLD/CANCELLED 중 하나, "ALL"(전체 조회), null/빈값(미지정 시 기본 OPEN)
    Integer minPrice,    // 현재가(currentPrice) 최소값. null이면 하한 없음
    Integer maxPrice     // 현재가(currentPrice) 최대값. null이면 상한 없음
) {
}