package com.bwm.item.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * POST /api/items 요청 바디.
 *
 * @param title        상품명. 필수, 최대 100자 (Item.title 컬럼과 동일 제약)
 * @param category     카테고리. 필수, 최대 45자 (Item.category 컬럼과 동일 제약)
 * @param startPrice   경매 시작가. 필수, 0보다 큰 값
 * @param auctionEndAt 경매 마감 시각. 필수, 미래 시각이어야 함
 * @param description  상품 설명. 선택, 최대 255자
 */
public record ItemCreateRequest(

                @NotBlank @Size(max = 100) String title,

                @NotBlank @Size(max = 45) String category,

                @NotNull @Positive Integer startPrice,

                @NotNull @Future LocalDateTime auctionEndAt,

                @Size(max = 255) String description) {

}
