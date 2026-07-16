package com.bwm.wallet.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class WalletHistoryResponseDto {
	private String type;
	private Integer itemId;
	private Integer amount;
	private Integer balanceAfter;  
	private LocalDateTime createdAt;
}
