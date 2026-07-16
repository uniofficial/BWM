package com.bwm.wallet.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
	public ResponseEntity<WalletResponseDto> getMyWallet(){
		// TODO: 추후 Spring Security 도입 시 @AuthenticationPrincipal 등을 통해 실제 로그인 유저 ID 획득 예정
		// 우선 1번 id로 하드코딩
        Integer tempUserId = 1; 
        
        WalletResponseDto walletDto = walletService.getMyWallet(tempUserId);
        return ResponseEntity.ok(walletDto);
	}
	
	/**
     * 💡 포인트 내역 조회 API 엔드포인트 추가
     */
    @GetMapping("/me/histories")
    public ResponseEntity<List<WalletHistoryResponseDto>> getMyWalletHistories() {
        // TODO: 추후 Spring Security 도입 시 @AuthenticationPrincipal 등을 통해 실제 로그인 유저 ID 획득 예정
        Integer tempUserId = 1; 
        
        List<WalletHistoryResponseDto> histories = walletHistoryService.getMyHistory(tempUserId);
        return ResponseEntity.ok(histories);
    }
}
