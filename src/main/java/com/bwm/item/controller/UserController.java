package com.bwm.item.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bwm.auction.dto.SoldAuctionResponse;
import com.bwm.auction.service.AuctionService;
import com.bwm.item.dto.response.ItemSummaryResponse;
import com.bwm.item.service.ItemService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Sort;

/**
 * 로그인한 사용자 본인("me") 기준 조회 API 모음.
 * "내가 등록한 상품" 하나만 구현되어 있고, "내가 판매 완료한 상품" 등은 순서대로 추가 예정.
 */
@RestController
@RequestMapping("/api/users/me")
@RequiredArgsConstructor
public class UserController {
    private final ItemService itemService;
    private final AuctionService auctionService;
    /**
     * 내가 등록한 상품 조회 API.
     *
     * @param userIdHeader 요청자 user_id.
     *        [임시 처리 - 인증 파트 미완성 상태] ItemController와 동일하게 X-USER-ID 헤더로 받는다.
     *        TODO: 인증 파트 완성되면 SecurityContext에서 로그인한 사용자 id를 꺼내는 방식으로 교체할 것.
     * @param pageable 페이지 조건 (기본값: 페이지당 20개, 등록 최신순)
     * @return 200 OK + 내가 등록한 상품 목록
     */
    @GetMapping("/items")
    public ResponseEntity<Page<ItemSummaryResponse>> getMyItems(
        @RequestHeader(value = "X-USER-ID", required = false) Integer userIdHeader,
        @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
             // "내" 상품을 조회하는 API라 누구 것인지 알아야 하므로 헤더 없으면 요청 자체를 막음
             if (userIdHeader == null) {
                throw new IllegalArgumentException("X-USER_ID 헤더가 필요합니다(임시 인증 방식- 인증 파트 연동 전까지");
             }
            Page<ItemSummaryResponse> response = itemService.getMyItems(userIdHeader, pageable);
            return ResponseEntity.ok(response);
        }

    
    // X-USER-ID 헤더 누락 등 요청 자체가 잘못된 경우는 400으로 응답
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumaentException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
    
    /**
     * 내가 등록한 상품 중 판매 완료(SOLD)된 것만 조회하는 API.
     *
     * 이미 AuctionController(GET /api/auctions/sold/me)에 동일한 기능이 구현돼 있어서,
     * 여기서는 새로 로직을 짜지 않고 AuctionService.getMySoldAuctions()를 그대로 재사용한다.
     * 스펙 명세서에 정의된 URL(/api/users/me/sales)로도 접근 가능하게 하기 위한 얇은 위임(delegate) 엔드포인트.
     *
     * @param userIdHeader 요청자 user_id (X-USER-ID 헤더, 임시 인증 방식)
     *       
     */
     // TODO: 인증 파트 완성되면 SecurityContext에서 로그인한 사용자 id를 꺼내는 방식으로 교체할 것
    @GetMapping("/sales")
    public ResponseEntity<List<SoldAuctionResponse>> getMySales(
        @RequestHeader(value = "X-USER-ID", required = false)
        Integer userIdHeader) {
            if (userIdHeader == null) {
                throw new IllegalArgumentException("X-USER-ID 헤더가 필요합니다. - 임시 인증 방식");
            }

            List<SoldAuctionResponse> response = auctionService.getMySoldAuctions(userIdHeader);
            return ResponseEntity.ok(response);

        }
    
    
    
}
