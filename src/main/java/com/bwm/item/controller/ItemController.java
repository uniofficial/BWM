package com.bwm.item.controller;

import com.bwm.item.dto.request.ItemCreateRequest;
import com.bwm.item.dto.response.ItemDetailResponse;
import com.bwm.item.dto.response.ItemResponse;
import com.bwm.item.dto.response.ItemSummaryResponse;
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
 * 지금은 "상품 등록" 하나만 구현되어 있고, 목록/상세/검색/수정/취소는 순서대로 추가 예정.
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
    
    
    }
    

