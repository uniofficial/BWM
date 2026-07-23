package com.bwm.wallet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.bwm.wallet.entity.WalletHistory;

public interface WalletHistoryRepository extends JpaRepository<WalletHistory, Integer> {
	// 필요하면 비즈니스 로직 추가
	// 유저의 포인트 내역 전체를 조회, 최신 거래 일수로
	List<WalletHistory> findByWalletUserIdOrderByCreatedAtDesc(Integer userId);

	// 지갑 삭제 시 내역도 삭제
	void deleteByWalletUserId(Integer userId);
}
