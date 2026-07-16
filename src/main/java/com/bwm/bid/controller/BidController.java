package com.bwm.bid.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bwm.bid.dto.request.BidCreateRequest;
import com.bwm.bid.dto.response.BidResponse;
import com.bwm.bid.dto.response.ItemBidHistoryResponse;
import com.bwm.bid.service.BidService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * 입찰 관련 API를 처리하는 컨트롤러
 */
@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
@Validated
public class BidController {

    private final BidService bidService;

    /**
     * 상품에 새로운 입찰 등록
     *
     * POST /api/items/{itemId}/bids
     *
     * @param itemId 입찰 대상 상품 ID
     * @param request 입찰 요청 정보
     * @return 등록된 입찰 정보
     */
    @PostMapping("/{itemId}/bids")
    public ResponseEntity<BidResponse> createBid(
            @PathVariable Integer itemId,
            @Valid @RequestBody BidCreateRequest request
    ) {
        /*
         * TODO
         * Spring Security 연동 후 로그인 사용자의 ID로 교체합니다.
         */
        Integer bidderId = 1;

        BidResponse response =
                bidService.createBid(itemId, bidderId, request);

        return ResponseEntity.ok(response);
    }

    /**
     * 특정 상품의 전체 입찰 내역을 최신 입찰순으로 조회
     *
     * 사용자 ID나 이메일은 반환하지 않고
     * 입찰자 닉네임만 반환함 
     *
     * GET /api/items/{itemId}/bids
     *
     * @param itemId 조회할 상품 ID
     * @return 상품별 입찰 내역
     */
    @GetMapping("/{itemId}/bids")
    public ResponseEntity<List<ItemBidHistoryResponse>> getItemBidHistory(
            @PathVariable Integer itemId
    ) {
        List<ItemBidHistoryResponse> responses =
                bidService.getItemBidHistory(itemId);

        return ResponseEntity.ok(responses);
    }
}