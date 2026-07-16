package com.bwm.bid.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * 입찰 등록 요청 DTO
 */
public record BidCreateRequest(

        @NotNull(message = "입찰 금액은 필수입니다.")
        @Min(value = 1, message = "입찰 금액은 1원 이상이어야 합니다.")
        Integer bidAmount
) {
}