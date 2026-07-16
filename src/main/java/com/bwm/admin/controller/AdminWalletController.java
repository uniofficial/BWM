package com.bwm.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bwm.wallet.dto.PointChargeRequestDto;
import com.bwm.wallet.service.WalletService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminWalletController {
	private final WalletService walletService;
	
	@PostMapping("/{userId}/wallet/charge")
	public ResponseEntity<String> chargeUserPoint(
			@PathVariable Integer userId,
            @RequestBody @Valid PointChargeRequestDto requestDto
			){
		walletService.chargeUserPoint(userId, requestDto.getAmount());
		
		return ResponseEntity.ok("포인트 충전이 성공적으로 완료되었습니다.");
	}
}
