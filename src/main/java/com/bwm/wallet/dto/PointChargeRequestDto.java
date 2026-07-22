package com.bwm.wallet.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PointChargeRequestDto {
	
	@NotNull(message = "충전할 금액은 필수 입력 항목입니다.")
	@Min(value = 1, message = "충전할 금액은 최소 1원 이상이어야 합니다.")
	private Integer amount;
}
