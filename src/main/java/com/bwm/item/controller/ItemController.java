package com.bwm.item.controller;

import com.bwm.item.dto.request.ItemCreateRequest;
import com.bwm.item.dto.request.ItemSearchCondition;
import com.bwm.item.dto.request.ItemUpdateRequest;
import com.bwm.item.dto.response.ItemDetailResponse;
import com.bwm.item.dto.response.ItemResponse;
import com.bwm.item.dto.response.ItemSummaryResponse;
import com.bwm.item.exception.ItemAccessDeniedException;
import com.bwm.item.exception.ItemNotFoundException;
import com.bwm.item.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
     * @param sellerIdHeader 판매자 user_id.
     *        [임시 처리 - 인증 파트 미완성 상태]
     *        현재는 로그인 기능이 없어서, 요청 헤더 "X-USER-ID"에 판매자 id를 직접 담아 테스트한다.
     *        예) Postman에서 Headers 탭에 X-USER-ID: 1 추가 후 요청.
     *
     *        TODO: 인증 파트(로그인/JWT) 완성되면 이 파라미터를 지우고,
     *              아래처럼 SecurityContext에서 로그인한 사용자 id를 꺼내는 방식으로 교체할 것.
     *              예) Long sellerId = ((CustomUserDetails) authentication.getPrincipal()).getUserId();
     *
     * @param request 상품 등록 요청 값. @Valid가 ItemCreateRequest에 붙은 검증 애너테이션들
     *                (@NotBlank, @Positive, @Future 등)을 자동으로 검사하고,
     *                실패 시 400 Bad Request를 응답한다 (별도 try-catch 필요 없음).
     * @return 201 Created + 등록된 상품 정보(ItemResponse)
     */

    @PostMapping
    public ResponseEntity<ItemResponse> createItem(
        @RequestHeader(value = "X-USER-ID", required = false) Integer sellerIdHeader,
        @Valid @RequestBody ItemCreateRequest request ) {

            // 헤더가 없으면(= 임시 인증 정보를 안 보낸 경우) 요청 자체를 막는다.
        // 실제 인증 붙으면 이 방어 코드는 자연스럽게 없어짐(로그인 안 하면 애초에 이 API에 못 들어옴).
        if (sellerIdHeader == null) {
        throw new IllegalArgumentException("X-USER-ID 헤더가 필요합니다. (임시 인증 방식 - 인증 파트 연동 전)");
        }

        // 실제 비즈니스 로직은  전부 Service 에 위임. Controller는 요청/응답 변환과 HTTP 상태 코드 결정만 담당
        ItemResponse response = itemService.createItem(sellerIdHeader, request);

        // 리소스가 새로 생성됐으므로 200이 아니라 201 Created로 응답
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

        }

     /**
     * 상품 목록 조회 API.
     *
     * 쿼리 파라미터로 페이지네이션 제어 가능 (예: ?page=0&size=20&sort=createdAt,desc).
     * 기본값: 페이지당 20개, 등록 최신순 정렬.
     *
     * @param pageable 페이지 조건 (Spring이 쿼리 파라미터를 자동으로 바인딩)
     * @return 200 OK + 페이징된 상품 목록
     */
    @GetMapping
    public ResponseEntity<Page<ItemSummaryResponse>> getItems(
        @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable){
            Page<ItemSummaryResponse> response = itemService.getItems(pageable);
            return ResponseEntity.ok(response);
        }

    /**
     * 상품 상세 조회 API.
     *
     * 등록/수정/취소와 달리 인증 헤더(X-USER-ID) 없이 누구나 호출 가능하게 열어둠
     * (로그인 안 한 사용자도 상품 상세는 볼 수 있어야 자연스러우므로).
     *
     * @param itemId 조회할 상품 id (URL 경로 변수, 예: /api/items/5 -> itemId=5)
     * @return 200 OK + 상품 상세 정보. 존재하지 않으면 404 (기존 ItemNotFoundException 핸들러가 처리)
     */
    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDetailResponse> getItem(@PathVariable Integer itemId){
        // 권한 체크가 없는 조회성 API라 Controller가 하는 일은 파라미터 받아서 Service 호출하고
        // 결과를 200으로 감싸는 것뿐 (createItem처럼 헤더 검증하는 코드가 없음)
        ItemDetailResponse response = itemService.getItem(itemId);

        return ResponseEntity.ok(response);
    }

    /**
     * 상품 검색 API.
     *
     * @ModelAttribute가 하는 일: HTTP 쿼리 파라미터들(?keyword=...&category=...)을
     * ItemSearchCondition 레코드의 각 필드에 이름으로 매칭해서 자동으로 채워준다.
     * (예: 쿼리 파라미터 "keyword"가 있으면 ItemSearchCondition.keyword에 들어감)
     * 안 보낸 파라미터는 자동으로 null이 되므로, 컨트롤러에서 따로 null 체크 코드를 안 짜도 됨.
     *
     * 예) GET /api/items/search?keyword=노트북&category=전자기기&minPrice=10000&maxPrice=50000&page=0&size=20
     *
     * @param condition 검색 조건 (쿼리 파라미터 자동 바인딩)
     * @param pageable 페이지 조건 (page/size/sort 쿼리 파라미터 자동 바인딩)
     * @return 200 OK + 조건에 맞는 상품 목록
     */
    @GetMapping("/search")
    public ResponseEntity<Page<ItemSummaryResponse>> searchItems(
        @ModelAttribute ItemSearchCondition condition,
        @PageableDefault(size = 20, sort = "createdAt",
            direction = Sort.Direction.DESC) Pageable pageable
        ) {
            // 검증/조립 로직은 전부 Service에 위임하고 Controller는 요청 바인딩 + 응답 포장만 담당
            Page<ItemSummaryResponse> response = itemService.searchItems(condition, pageable);
            return ResponseEntity.ok(response);
        }

     /**
     * 상품 수정 API.
     *
     * @param sellerIdHeader 요청자 user_id (X-USER-ID 헤더, 임시 인증 방식 - createItem과 동일)
     *        TODO: 인증 파트 완성되면 SecurityContext에서 로그인한 사용자 id를 꺼내는 방식으로 교체할 것.
     * @param itemId 수정할 상품 id
     * @param request 수정할 값들. title/category/description 전부 선택사항이며 보낸 필드만 반영됨
     * @return 200 OK + 수정된 상품 정보. 본인 상품 아니면 403, 없으면 404, OPEN 아니거나 입찰 있으면 409
     */
    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemResponse> updateItem(
        @RequestHeader(value = "X-USER-ID", required = false)
        Integer sellerIdHeader,

        @PathVariable
        Integer itemId,

        @Valid
        @RequestBody
        ItemUpdateRequest request){
            if (sellerIdHeader == null) {
                throw new IllegalArgumentException("X-USER-ID 헤더가 필요합니다. (임시 인증 방식)");
            }

            ItemResponse response = itemService.updateItem(itemId, sellerIdHeader, request);
            return ResponseEntity.ok(response);
        }

    /**
     * 상품 취소 API.
     *
     * @param sellerIdHeader 요청자 user_id (X-USER-ID 헤더, 임시 인증 방식)
     *        TODO: 인증 파트 완성되면 SecurityContext에서 로그인한 사용자 id를 꺼내는 방식으로 교체할 것.
     * @param itemId 취소할 상품 id
     * @return 200 OK + 취소된 상품 정보. 본인 상품 아니면 403, 없으면 404, OPEN 아니거나 입찰 있으면 409
     */
    @PostMapping("/{itemId}/cancel")
    public ResponseEntity<ItemResponse> cancelItem(
        @RequestHeader(value = "X-USER-ID", required = false)
        Integer sellerIdHeader,

        @PathVariable
        Integer itemId ) {
            if(sellerIdHeader == null) {
                throw new IllegalArgumentException("X-USER-ID 헤더가 필요합니다. (임시 인증 방식)");
            }

        ItemResponse response = itemService.cancelItem(itemId, sellerIdHeader);

        return ResponseEntity.ok(response);
    }

    // X-USER-ID 헤더 누락 등 요청 자체가 잘못된 경우는 400으로 응답 (전역 예외 처리기 도입 전 임시 처리)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    // 존재하지 않는 사용자/상품 등 리소스를 찾을 수 없는 경우는 404로 응답 (전역 예외 처리기 도입 전 임시 처리)
    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<String> handleItemNotFoundException(ItemNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    // 본인 상품이 아닌데 수정/취소하려 하면 403으로 응답
    @ExceptionHandler(ItemAccessDeniedException.class)
    public ResponseEntity<String> handleItemAccessDeniedException(ItemAccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }

    // OPEN 상태가 아니거나 이미 입찰이 들어온 상품을 수정/취소하려 하면 409(Conflict)로 응답
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalStateException(IllegalStateException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }
}
