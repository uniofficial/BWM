package com.bwm.auction.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
     * 로그인한 판매자가 특정 상품의 경매를 종료합니다.
     *
     * POST /api/items/{itemId}/close
     *
     * JWT 인증 정보에서 로그인 사용자의 이메일을 추출하고,
     * 해당 사용자가 상품 판매자인 경우에만 경매를 종료합니다.
     *
     * @param itemId 종료할 상품 ID
     * @param authentication Spring Security 인증 정보
     * @return 경매 종료 결과
     */
    @PostMapping("/api/items/{itemId}/close")
    public ResponseEntity<AuctionCloseResponse> closeAuction(
            @PathVariable("itemId") Integer itemId,
            Authentication authentication
    ) {
        String requesterEmail = authentication.getName();

        AuctionCloseResponse response =
                auctionService.closeAuction(
                        itemId,
                        requesterEmail
                );

        return ResponseEntity.ok(response);
    }

    /**
     * 로그인 사용자의 낙찰 내역을 조회합니다.
     *
     * GET /api/auctions/wins/me
     *
     * JWT 인증 정보에서 로그인 사용자의 이메일을 추출하므로
     * X-USER-ID 헤더나 사용자 ID 파라미터를 받지 않습니다.
     *
     * @param authentication Spring Security 인증 정보
     * @return 낙찰 완료 상품 목록
     */
    @GetMapping("/api/auctions/wins/me")
    public ResponseEntity<List<WinningAuctionResponse>> getMyWinningAuctions(
            Authentication authentication
    ) {
        String userEmail = authentication.getName();

        List<WinningAuctionResponse> response =
                auctionService.getMyWinningAuctions(userEmail);

        return ResponseEntity.ok(response);
    }

    /**
     * 로그인 사용자의 판매 완료 내역을 조회합니다.
     *
     * GET /api/auctions/sold/me
     *
     * JWT 인증 정보에서 로그인 사용자의 이메일을 추출하므로
     * X-USER-ID 헤더나 사용자 ID 파라미터를 받지 않습니다.
     *
     * @param authentication Spring Security 인증 정보
     * @return 판매 완료 상품 목록
     */
    @GetMapping("/api/auctions/sold/me")
    public ResponseEntity<List<SoldAuctionResponse>> getMySoldAuctions(
            Authentication authentication
    ) {
        String userEmail = authentication.getName();

        List<SoldAuctionResponse> response =
                auctionService.getMySoldAuctions(userEmail);

        return ResponseEntity.ok(response);
    }
}