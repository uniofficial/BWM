package com.bwm.bid.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.bwm.bid.dto.request.BidCreateRequest;
import com.bwm.bid.dto.response.BidResponse;
import com.bwm.bid.service.BidService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
@Validated
public class BidController {

    private final BidService bidService;

    /**
     * 상품 입찰
     *
     * POST /api/items/{itemId}/bids
     */
    @PostMapping("/{itemId}/bids")
    public ResponseEntity<BidResponse> createBid(
            @PathVariable Integer itemId,
            @Valid @RequestBody BidCreateRequest request
    ) {

        /*
         * TODO
         * Security 적용 후 로그인 사용자 ID로 교체!!! 
         */
        Integer bidderId = 1;

        BidResponse response =
                bidService.createBid(itemId, bidderId, request);

        return ResponseEntity.ok(response);
    }
}