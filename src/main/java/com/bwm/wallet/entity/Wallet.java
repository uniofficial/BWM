package com.bwm.wallet.entity;

import com.bwm.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Table(name="wallet")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Wallet {
	@Id
	@Column(name = "user_id")
	private Integer userId;
	

	@OneToOne(fetch = FetchType.LAZY) 
    @MapsId  // wallet의 pk를 user의 pk와 연결
    @JoinColumn(name = "user_id") 
    private User user;
	
	@Column(name="balance", nullable = false)
	private Integer balance;
	
	/*
     * 포인트 충전 및 환불 (잔액 증가)
     */
    public void charge(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("충전 및 환불 금액은 1원 이상이어야 합니다.");
        }
        this.balance += amount;
    }
    
    /*
     * 포인트 사용 및 차감 (잔액 감소)
     * 입찰 시 포인트를 차감할 때 호출됩니다.
     */
    public void deduct(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("차감할 금액은 1원 이상이어야 합니다.");
        }
        if (this.balance < amount) {
            throw new IllegalArgumentException("보유 포인트 잔액이 부족합니다.");
        }
        this.balance -= amount;
    }
}
