package com.bwm.bid.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bwm.bid.dto.response.MyBidHistoryResponse;
import com.bwm.bid.service.BidService;

import lombok.RequiredArgsConstructor;

/**
 * 로그인 사용자의 입찰 관련 API를 처리하는 컨트롤러
 */
@RestController
@RequestMapping("/api/users/me")
@RequiredArgsConstructor
public class MyBidController {

    private final BidService bidService;

    /**
     * 로그인 사용자의 전체 입찰 내역을 최신순으로 조회합니다.
     *
     * GET /api/users/me/bids
     *
     * JWT 인증 정보에서 로그인 사용자의 이메일을 추출하므로
     * X-USER-ID 헤더나 사용자 ID 파라미터를 받지 않습니다.
     *
     * @param authentication Spring Security 인증 정보
     * @return 로그인 사용자의 전체 입찰 내역
     */
    @GetMapping("/bids")
    public ResponseEntity<List<MyBidHistoryResponse>> getMyBidHistory(
            Authentication authentication
    ) {
        String userEmail = authentication.getName();

        List<MyBidHistoryResponse> responses =
                bidService.getMyBidHistory(userEmail);

        return ResponseEntity.ok(responses);
    }
}