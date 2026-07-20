package com.bwm.wallet.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bwm.wallet.dto.WalletHistoryResponseDto;
import com.bwm.wallet.dto.WalletResponseDto;
import com.bwm.wallet.service.WalletHistoryService;
import com.bwm.wallet.service.WalletService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletController {
	private final WalletService walletService;
	
	private final WalletHistoryService walletHistoryService;
	
	@GetMapping("/me")
    public ResponseEntity<WalletResponseDto> getMyWallet(
            @RequestHeader("X-USER-ID") Integer userId 
    ) {
        WalletResponseDto walletDto = walletService.getMyWallet(userId);
        return ResponseEntity.ok(walletDto);
    }
	
	/**
     * 💡 포인트 내역 조회 API 엔드포인트 추가
     */
	@GetMapping("/me/histories")
    public ResponseEntity<List<WalletHistoryResponseDto>> getMyWalletHistories(
            @RequestHeader("X-USER-ID") Integer userId 
    ) {
        List<WalletHistoryResponseDto> histories = walletHistoryService.getMyHistory(userId);
        return ResponseEntity.ok(histories);
    }
}
