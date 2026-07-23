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
     *
     * @param authentication JWT 인증 정보. email(subject)을 그대로 서비스에 넘기면
     *                       서비스가 내부에서 로그인 사용자를 조회한다.
     * @param request        상품 등록 요청 값. @Valid가 ItemCreateRequest에 붙은 검증 애너테이션들
     *                       (@NotBlank, @Positive, @Future 등)을 자동으로 검사하고,
     *                       실패 시 400 Bad Request를 응답한다 (별도 try-catch 필요 없음).
     * @return 201 Created + 등록된 상품 정보(ItemResponse)
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
     *
     * @param authentication JWT 인증 정보 (요청자, createItem과 동일)
     * @param itemId         수정할 상품 id
     * @param request        수정할 값들. title/category/description 전부 선택사항이며 보낸 필드만 반영됨
     * @return 200 OK + 수정된 상품 정보. 본인 상품 아니면 403, 없으면 404, OPEN 아니거나 입찰 있으면 409
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
     *
     * @param authentication JWT 인증 정보 (요청자)
     * @param itemId         취소할 상품 id
     * @return 200 OK + 취소된 상품 정보. 본인 상품 아니면 403, 없으면 404, OPEN 아니거나 입찰 있으면 409
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