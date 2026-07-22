package com.bwm.wallet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bwm.wallet.entity.WalletChargeRequest;

public interface WalletChargeRequestRepository extends JpaRepository<WalletChargeRequest, Integer> {

    @Query("SELECT r FROM WalletChargeRequest r JOIN FETCH r.user WHERE r.user.email = :email ORDER BY r.createdAt DESC")
    List<WalletChargeRequest> findByUserEmailOrderByCreatedAtDesc(@Param("email") String email);

    @Query("SELECT r FROM WalletChargeRequest r JOIN FETCH r.user ORDER BY r.createdAt DESC")
    List<WalletChargeRequest> findAllByOrderByCreatedAtDesc();
}
