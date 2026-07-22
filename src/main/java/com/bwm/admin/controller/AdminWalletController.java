package com.bwm.admin.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bwm.global.dto.ResultDto;
import com.bwm.wallet.dto.PointChargeRequestDto;
import com.bwm.wallet.dto.WalletChargeRequestResponseDto;
import com.bwm.wallet.service.WalletService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminWalletController {
	private final WalletService walletService;

	@PostMapping("/users/{userId}/wallet/charge")
	public ResponseEntity<ResultDto<Void>> chargeUserPoint(
			@PathVariable("userId") Integer userId,
			@RequestBody @Valid PointChargeRequestDto requestDto) {
		walletService.chargeUserPoint(userId, requestDto.getAmount());

		return ResponseEntity.ok(ResultDto.success(null));
	}

	@GetMapping("/users/user-list")
	public ResponseEntity<ResultDto<String>> getUserList() {
		return ResponseEntity.ok(ResultDto.success("ADMIN"));
	}

	/**
	 * 모든 유저의 포인트 충전 요청 목록 조회 API 엔드포인트
	 */
	@GetMapping("/wallets/charge/requests")
	public ResponseEntity<ResultDto<List<WalletChargeRequestResponseDto>>> getAllChargeRequests() {
		List<WalletChargeRequestResponseDto> requests = walletService.getAllChargeRequests();
		return ResponseEntity.ok(ResultDto.success(requests));
	}

	/**
	 * 특정 포인트 충전 요청 승인 API 엔드포인트
	 */
	@PostMapping("/wallets/charge/requests/{requestId}/approve")
	public ResponseEntity<ResultDto<Void>> approvePointCharge(
			@PathVariable("requestId") Integer requestId
	) {
		walletService.approvePointCharge(requestId);
		return ResponseEntity.ok(ResultDto.success(null));
	}

	/**
	 * 특정 포인트 충전 요청 반려 API 엔드포인트
	 */
	@PostMapping("/wallets/charge/requests/{requestId}/reject")
	public ResponseEntity<ResultDto<Void>> rejectPointCharge(
			@PathVariable("requestId") Integer requestId
	) {
		walletService.rejectPointCharge(requestId);
		return ResponseEntity.ok(ResultDto.success(null));
	}
}
