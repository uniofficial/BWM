package com.bwm.wallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bwm.wallet.entity.Wallet;

public interface WalletRepository extends JpaRepository<Wallet, Integer> {
	// 필요하면 비즈니스 로직 추가
}
