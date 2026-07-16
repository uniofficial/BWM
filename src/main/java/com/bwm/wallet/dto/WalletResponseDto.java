package com.bwm.wallet.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WalletResponseDto {
    private Integer balance; // 현재 포인트 잔액만 깔끔하게 반환!
}
