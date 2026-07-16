package com.bwm.wallet.service;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import com.bwm.wallet.dto.WalletHistoryResponseDto;
import com.bwm.wallet.dto.WalletResponseDto;
import com.bwm.wallet.entity.Wallet;
import com.bwm.wallet.entity.WalletHistory;
import com.bwm.wallet.entity.WalletHistoryType;
import com.bwm.wallet.repository.WalletHistoryRepository;
import com.bwm.wallet.repository.WalletRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WalletService {
	private final WalletRepository walletRepository;
	
	private final WalletHistoryRepository walletHistoryRepository;
	
	public WalletResponseDto getMyWallet(Integer userId) {
        Wallet wallet = walletRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저의 지갑을 찾을 수 없습니다."));
        
        return new WalletResponseDto(wallet.getBalance());
    }
	
	/*
	 * 사용자 포인트 충전 비즈니스 로직 추가
	 */
	@Transactional
	public void chargeUserPoint(Integer userId, Integer amount) {
		// TODO: llegalArgumentException 부분은 전역예외처리기 만들면 나중에 바꿈
		Wallet wallet = walletRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저의 지갑을 찾을 수 없습니다."));
		
		wallet.charge(amount);
		
		WalletHistory history = WalletHistory.builder()
											 .wallet(wallet)
											 .itemId(null)
											 .type(WalletHistoryType.CHARGE)
											 .amount(amount)
											 .balanceAfter(wallet.getBalance())
											 .build();
		
		walletHistoryRepository.save(history);
	}
}
