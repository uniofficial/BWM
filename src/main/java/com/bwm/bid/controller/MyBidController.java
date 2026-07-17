package com.bwm.bid.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bwm.bid.dto.response.MyBidHistoryResponse;
import com.bwm.bid.service.BidService;

import lombok.RequiredArgsConstructor;

/**
 * 로그인 사용자의 입찰 관련 API를 처리하는 컨트롤러 
 */
@RestController
@RequestMapping("/api/bids")
@RequiredArgsConstructor
@Validated
public class MyBidController {

    private final BidService bidService;

    /**
     * 로그인 사용자의 전체 입찰 내역을 최신순으로 조회합니다.
     *
     * GET /api/bids/me
     *
     * 인증 기능 연동 전까지 X-USER-ID 헤더를 통해
     * 요청 사용자를 임시로 식별합니다.
     *
     * @param userId 요청 사용자 ID
     * @return 사용자의 전체 입찰 내역
     */
    @GetMapping("/me")
    public ResponseEntity<List<MyBidHistoryResponse>> getMyBidHistory(
            @RequestHeader("X-USER-ID") Integer userId
    ) {
        List<MyBidHistoryResponse> responses =
                bidService.getMyBidHistory(userId);

        return ResponseEntity.ok(responses);
    }
}