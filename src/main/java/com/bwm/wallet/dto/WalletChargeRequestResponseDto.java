package com.bwm.wallet.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletChargeRequestResponseDto {
    private Integer chargeRequestId;
    private Integer userId;
    private String userEmail;
    private Integer amount;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
}
