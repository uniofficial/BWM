package com.bwm.item.controller;

import com.bwm.item.dto.request.ItemCreateRequest;
import com.bwm.item.dto.request.ItemSearchCondition;
import com.bwm.item.dto.request.ItemUpdateRequest;
import com.bwm.item.dto.response.ItemDetailResponse;
import com.bwm.item.dto.response.ItemResponse;
import com.bwm.item.dto.response.ItemSummaryResponse;
import com.bwm.item.service.ItemService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 상품(Item) 관련 REST API 엔드포인트.
 */
@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    /**
     * 상품 등록 API.
     */
    @PostMapping
    public ResponseEntity<ItemResponse> createItem(
            Authentication authentication,
            @Valid @RequestBody ItemCreateRequest request
    ) {
        ItemResponse response =
                itemService.createItem(authentication.getName(), request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * 상품 목록 및 검색 API.
     *
     * 검색 조건을 보내지 않으면 OPEN 상태 상품을 조회한다.
     *
     * 사용 예시:
     * GET /api/items
     * GET /api/items?keyword=굿즈
     * GET /api/items?status=OPEN
     * GET /api/items?keyword=굿즈&status=OPEN
     * GET /api/items?category=전자기기
     * GET /api/items?minPrice=10000&maxPrice=50000
     *
     * 페이지네이션 예시:
     * GET /api/items?page=0&size=20&sort=createdAt,desc
     */
    @GetMapping
    public ResponseEntity<Page<ItemSummaryResponse>> getItems(
            @ModelAttribute ItemSearchCondition condition,
            @PageableDefault(
                    size = 20,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        Page<ItemSummaryResponse> response =
                itemService.getItems(condition, pageable);

        return ResponseEntity.ok(response);
    }

    /**
     * 상품 상세 조회 API.
     */
    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDetailResponse> getItem(
            @PathVariable("itemId") Integer itemId
    ) {
        ItemDetailResponse response = itemService.getItem(itemId);

        return ResponseEntity.ok(response);
    }

    /**
     * 기존 상품 검색 API.
     *
     * 기존 프론트엔드 코드와의 호환을 위해 일단 유지한다.
     * 신규 프론트엔드에서는 GET /api/items를 사용하는 것을 권장한다.
     */
    @GetMapping("/search")
    public ResponseEntity<Page<ItemSummaryResponse>> searchItems(
            @ModelAttribute ItemSearchCondition condition,
            @PageableDefault(
                    size = 20,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        Page<ItemSummaryResponse> response =
                itemService.getItems(condition, pageable);

        return ResponseEntity.ok(response);
    }

    /**
     * 상품 수정 API.
     */
    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemResponse> updateItem(
            Authentication authentication,
            @PathVariable("itemId") Integer itemId,
            @Valid @RequestBody ItemUpdateRequest request
    ) {
        ItemResponse response = itemService.updateItem(
                itemId,
                authentication.getName(),
                request
        );

        return ResponseEntity.ok(response);
    }

    /**
     * 상품 취소 API.
     */
    @PostMapping("/{itemId}/cancel")
    public ResponseEntity<ItemResponse> cancelItem(
            Authentication authentication,
            @PathVariable("itemId") Integer itemId
    ) {
        ItemResponse response = itemService.cancelItem(
                itemId,
                authentication.getName()
        );

        return ResponseEntity.ok(response);
    }
}