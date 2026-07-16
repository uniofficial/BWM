package com.bwm.auction.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bwm.auction.dto.AuctionCloseResponse;
import com.bwm.auction.service.AuctionService;

//경매 종료 관련 API 처리 컨트롤러
@RestController
@RequestMapping("/api/items")
public class AuctionController {

    private final AuctionService auctionService;

    public AuctionController(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    /**
     * 특정 상품의 경매 종료
     *
     * POST /api/items/{itemId}/close
     *
     * @param itemId 종료할 상품 ID
     * @return 경매 종료 결과
     */
    @PostMapping("/{itemId}/close")
    public ResponseEntity<AuctionCloseResponse> closeAuction(
            @PathVariable Long itemId) {

        /*
         * TODO
         * 인증 기능이 완성되면 SecurityContext에서
         * 현재 로그인한 사용자의 ID를 가져오도록 수정 
         */
        Long requesterId = 1L;

        AuctionCloseResponse response =
                auctionService.closeAuction(itemId, requesterId);

        return ResponseEntity.ok(response);
    }
}