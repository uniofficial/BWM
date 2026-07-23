package com.bwm.wallet.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bwm.wallet.entity.WalletChargeRequest;

import jakarta.persistence.LockModeType;

public interface WalletChargeRequestRepository extends JpaRepository<WalletChargeRequest, Integer> {

    @Query("SELECT r FROM WalletChargeRequest r JOIN FETCH r.user WHERE r.user.userUuid = :userUuid ORDER BY r.createdAt DESC")
    List<WalletChargeRequest> findByUserUuidOrderByCreatedAtDesc(@Param("userUuid") String userUuid);

    @Query("SELECT r FROM WalletChargeRequest r JOIN FETCH r.user ORDER BY r.createdAt DESC")
    List<WalletChargeRequest> findAllByOrderByCreatedAtDesc();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM WalletChargeRequest r JOIN FETCH r.user WHERE r.chargeRequestId = :requestId")
    Optional<WalletChargeRequest> findByIdForUpdate(@Param("requestId") Integer requestId);
}
