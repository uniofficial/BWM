package com.bwm.wallet.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bwm.wallet.dto.WalletResponseDto;
import com.bwm.wallet.entity.Wallet;
import com.bwm.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 더티 체킹(변경 감지)을 위한 복사본(스냅샷)을 생성하지 않아서 덕분에 메모리가 절약되고 DB 읽기 성능이 소폭 향상된다고 합니다.
public class WalletService {
	private final WalletRepository walletRepository;
	
	public WalletResponseDto getMyWallet(Integer userId) {
        Wallet wallet = walletRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저의 지갑을 찾을 수 없습니다."));
        
        return new WalletResponseDto(wallet.getBalance());
    }
}
