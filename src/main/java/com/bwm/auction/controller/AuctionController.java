package com.bwm.auction.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bwm.auction.dto.AuctionCloseResponse;
import com.bwm.auction.service.AuctionService;

import lombok.RequiredArgsConstructor;

/**
 * 경매 종료 관련 API를 처리하는 컨트롤러입니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/items")
public class AuctionController {

    private final AuctionService auctionService;

    /**
     * 특정 상품의 경매를 종료합니다.
     *
     * POST /api/items/{itemId}/close
     *
     * @param itemId 종료할 상품 ID
     * @return 경매 종료 결과
     */
    @PostMapping("/{itemId}/close")
    public ResponseEntity<AuctionCloseResponse> closeAuction(
            @PathVariable Integer itemId) {

        /*
         * TODO
         * 인증 기능이 최종 연결되면 하드코딩 값을 제거하고
         * SecurityContext 또는 @AuthenticationPrincipal에서
         * 현재 로그인 사용자의 ID를 가져오도록 변경합니다.
         */
        Integer requesterId = 1;

        AuctionCloseResponse response =
                auctionService.closeAuction(itemId, requesterId);

        return ResponseEntity.ok(response);
    }
}