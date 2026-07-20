package com.bwm.wallet.entity;

import java.time.LocalDateTime;

import com.bwm.item.entity.Item;

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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "wallethistory")
@Getter
@NoArgsConstructor
public class WalletHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wallet_history_id")
    private Integer walletHistoryId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_user_id", nullable = false)
    private Wallet wallet;
    
    @ManyToOne(fetch = FetchType.LAZY)
  	@JoinColumn(name = "item_id")
  	private Item item; 

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private WalletHistoryType type;
    
    @Column(name = "amount", nullable = false)
    private Integer amount;
    
    @Column(name = "balance_after", nullable = false)
    private Integer balanceAfter;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Builder
    public WalletHistory(Wallet wallet, Item item, WalletHistoryType type, 
                         Integer amount, Integer balanceAfter) {
        this.wallet = wallet;
        this.item = item;
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.createdAt = LocalDateTime.now(); // 생성 시점 자동 세팅
    }
}
