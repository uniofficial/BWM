package com.bwm.wallet.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import com.bwm.wallet.entity.Wallet;

import io.lettuce.core.dynamic.annotation.Param;
import jakarta.persistence.LockModeType;

public interface WalletRepository extends JpaRepository<Wallet, Integer> {
	// 필요하면 비즈니스 로직 추가
	@Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM Wallet w WHERE w.userId = :userId")
    Optional<Wallet> findByIdForUpdate(@Param("userId") Integer userId);
}
