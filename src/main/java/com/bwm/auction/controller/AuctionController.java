package com.bwm.auction.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.bwm.auction.dto.AuctionCloseResponse;
import com.bwm.auction.dto.SoldAuctionResponse;
import com.bwm.auction.dto.WinningAuctionResponse;
import com.bwm.auction.service.AuctionService;

import lombok.RequiredArgsConstructor;

/**
 * 경매 종료 및 낙찰 내역 관련 API를 처리하는 컨트롤러입니다.
 */
@RestController
@RequiredArgsConstructor
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
    @PostMapping("/api/items/{itemId}/close")
    public ResponseEntity<AuctionCloseResponse> closeAuction(
            @PathVariable Integer itemId
    ) {
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

    /**
     * 로그인 사용자의 낙찰 내역을 조회합니다.
     *
     * GET /api/auctions/wins/me
     *
     * 현재 인증 기능이 연결되지 않았으므로
     * X-USER-ID 헤더로 사용자 ID를 전달받습니다.
     *
     * @param userIdHeader 로그인 사용자 ID
     * @return 낙찰 완료 상품 목록
     */
    @GetMapping("/api/auctions/wins/me")
    public ResponseEntity<List<WinningAuctionResponse>> getMyWinningAuctions(
            @RequestHeader(value = "X-USER-ID", required = false)
            Integer userIdHeader
    ) {
        if (userIdHeader == null) {
            throw new IllegalArgumentException(
                    "X-USER-ID 헤더가 필요합니다. "
                    + "(임시 인증 방식 - 인증 파트 연동 전)"
            );
        }

        List<WinningAuctionResponse> response =
                auctionService.getMyWinningAuctions(userIdHeader);

        return ResponseEntity.ok(response);
    }

    /**
     * 로그인 사용자의 판매 완료 내역을 조회합니다.
     *
     * GET /api/auctions/sold/me
     *
     * 현재 인증 기능이 연결되지 않았으므로
     * X-USER-ID 헤더로 사용자 ID를 전달받습니다.
     *
     * @param userIdHeader 로그인 사용자 ID
     * @return 판매 완료 상품 목록
     */
    @GetMapping("/api/auctions/sold/me")
    public ResponseEntity<List<SoldAuctionResponse>> getMySoldAuctions(
            @RequestHeader(value = "X-USER-ID", required = false)
            Integer userIdHeader
    ) {
        if (userIdHeader == null) {
            throw new IllegalArgumentException(
                    "X-USER-ID 헤더가 필요합니다. "
                    + "(임시 인증 방식 - 인증 파트 연동 전)"
            );
        }

        List<SoldAuctionResponse> response =
                auctionService.getMySoldAuctions(userIdHeader);

        return ResponseEntity.ok(response);
    }

}