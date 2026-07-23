package com.bwm.wallet.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bwm.global.dto.ResultDto;
import com.bwm.wallet.dto.PointChargeRequestDto;
import com.bwm.wallet.dto.WalletChargeRequestResponseDto;
import com.bwm.wallet.dto.WalletHistoryResponseDto;
import com.bwm.wallet.dto.WalletResponseDto;
import com.bwm.wallet.service.WalletHistoryService;
import com.bwm.wallet.service.WalletService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletController {
	private final WalletService walletService;
	
	private final WalletHistoryService walletHistoryService;
	
	@GetMapping("/me")
    public ResponseEntity<ResultDto<WalletResponseDto>> getMyWallet(
    		Authentication authentication 
    ) {
		String userUuid = authentication.getName(); // 추가
        WalletResponseDto walletDto = walletService.getMyWallet(userUuid);
        return ResponseEntity.ok(ResultDto.success(walletDto));
    }
	
	/**
     * 💡 포인트 내역 조회 API 엔드포인트 추가
     */
	@GetMapping("/me/histories")
    public ResponseEntity<ResultDto<List<WalletHistoryResponseDto>>>  getMyWalletHistories(
    		Authentication authentication
    ) {
		String userUuid = authentication.getName(); // 추가
        List<WalletHistoryResponseDto> histories = walletHistoryService.getMyHistory(userUuid);
        return ResponseEntity.ok(ResultDto.success(histories));
    }

	/**
	 * 유저 포인트 충전 요청 API 엔드포인트
	 */
	@PostMapping("/charge/requests")
	public ResponseEntity<ResultDto<WalletChargeRequestResponseDto>> requestPointCharge(
			Authentication authentication,
			@RequestBody @Valid PointChargeRequestDto requestDto
	) {
		String userUuid = authentication.getName();
		WalletChargeRequestResponseDto response = walletService.requestPointCharge(userUuid, requestDto.getAmount());
		return ResponseEntity.ok(ResultDto.success(response));
	}

	/**
	 * 유저 본인의 포인트 충전 요청 내역 조회 API 엔드포인트
	 */
	@GetMapping("/charge/requests")
	public ResponseEntity<ResultDto<List<WalletChargeRequestResponseDto>>> getMyChargeRequests(
			Authentication authentication
	) {
		String userUuid = authentication.getName();
		List<WalletChargeRequestResponseDto> responses = walletService.getMyChargeRequests(userUuid);
		return ResponseEntity.ok(ResultDto.success(responses));
	}
}
