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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;

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
     * @param authentication JWT 인증 정보. email(subject)을 그대로 서비스에 넘기면
     *                       서비스가 내부에서 로그인 사용자를 조회한다.
     * @param pageable       페이지 조건 (기본값: 페이지당 20개, 등록 최신순)
     * @return 200 OK + 내가 등록한 상품 목록
     */
    @GetMapping("/items")
    public ResponseEntity<Page<ItemSummaryResponse>> getMyItems(
            Authentication authentication,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ItemSummaryResponse> response = itemService.getMyItems(authentication.getName(), pageable);
        return ResponseEntity.ok(response);
    }

    // 예외 처리는 GlobalExceptionHandler(전역)에서 일괄 처리한다.

    /**
     * 내가 등록한 상품 중 판매 완료(SOLD)된 것만 조회하는 API.
     *
     * 이미 AuctionController(GET /api/auctions/sold/me)에 동일한 기능이 구현돼 있어서,
     * 여기서는 새로 로직을 짜지 않고 AuctionService.getMySoldAuctions()를 그대로 재사용한다.
     * 스펙 명세서에 정의된 URL(/api/users/me/sales)로도 접근 가능하게 하기 위한 얇은 위임(delegate) 엔드포인트.
     *
     * @param authentication JWT 인증 정보 (로그인한 사용자)
     */
    @GetMapping("/sales")
    public ResponseEntity<List<SoldAuctionResponse>> getMySales(
            Authentication authentication) {

        List<SoldAuctionResponse> response = auctionService.getMySoldAuctions(authentication.getName());
        return ResponseEntity.ok(response);

    }

}