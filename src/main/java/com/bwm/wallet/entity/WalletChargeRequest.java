package com.bwm.wallet.entity;

import java.time.LocalDateTime;

import com.bwm.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "wallet_charge_request")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletChargeRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "charge_request_id")
    private Integer chargeRequestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "amount", nullable = false)
    private Integer amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ChargeRequestStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    public void approve() {
        if (this.status != ChargeRequestStatus.PENDING) {
            throw new IllegalStateException("대기 상태인 요청만 승인할 수 있습니다.");
        }
        this.status = ChargeRequestStatus.APPROVED;
        this.processedAt = LocalDateTime.now();
    }

    public void reject() {
        if (this.status != ChargeRequestStatus.PENDING) {
            throw new IllegalStateException("대기 상태인 요청만 거절할 수 있습니다.");
        }
        this.status = ChargeRequestStatus.REJECTED;
        this.processedAt = LocalDateTime.now();
    }
}
