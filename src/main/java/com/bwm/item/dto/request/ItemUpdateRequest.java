package com.bwm.item.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * PATCH /api/items/{itemId} 요청 바디.
 * 모든 필드가 선택사항 — 보낸 필드만 수정에 반영됨.
 *
 * 단, 이미 입찰이 시작된 상품은 description 외 필드가 하나라도 들어오면 예외가 발생함
 * (Item.update()에서 검증 — 스펙: "입찰 있으면 설명만 수정 가능").
 */
public record ItemUpdateRequest(
    @Size(max = 100)
    String title,

    @Size(max = 45)
    String category,

    @Size(max = 255)
    String description,

    @Positive
    Integer startPrice,

    @Future
    LocalDateTime auctionEndAt
) { }
