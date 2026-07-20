package com.bwm.item.dto.request;

import jakarta.validation.constraints.Size;

/**
 * PATCH /api/items/{itemId} 요청 바디.
 * PATCH이므로 모든 필드가 선택사항 — 보낸 필드만 수정에 반영되고, 안 보낸(null) 필드는 그대로 유지된다.
 * (title/category만 보내고 description은 안 보내면 description은 안 바뀜)
 */

public record ItemUpdateRequest(
    @Size(max = 100)
    String title,

    @Size(max = 45)
    String category,

    @Size(max=255)
    String Description
) {
  


}
