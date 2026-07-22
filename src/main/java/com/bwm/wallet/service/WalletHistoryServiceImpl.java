package com.bwm.wallet.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bwm.user.entity.User;
import com.bwm.user.entity.UserStatus;
import com.bwm.user.repository.UserRepository;
import com.bwm.wallet.dto.WalletHistoryResponseDto;
import com.bwm.wallet.entity.WalletHistory;
import com.bwm.wallet.repository.WalletHistoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WalletHistoryServiceImpl implements WalletHistoryService {
    
    private final WalletHistoryRepository walletHistoryRepository;
    
    private final UserRepository userRepository;
    
    @Override
    public List<WalletHistoryResponseDto> getMyHistory(String userEmail) { // 파라미터 변경
        User user = getUserByEmail(userEmail); // 이메일로 유저 정보 조회
        List<WalletHistory> histories = walletHistoryRepository.findByWalletUserIdOrderByCreatedAtDesc(user.getUserId()); // 기존 userId 대신 user.getUserId() 사용
        
        return histories.stream().map( history -> WalletHistoryResponseDto.builder()
                                                                          .type(history.getType().name())
                                                                          .itemId(history.getItem() != null ? history.getItem().getItemId() : null)
                                                                          .amount(history.getAmount())
                                                                          .balanceAfter(history.getBalanceAfter())
                                                                          .createdAt(history.getCreatedAt())
                                                                          .build())
                        .collect(Collectors.toList());
    }
    // 이메일 기반 유저 조회 공통 헬퍼 메서드 추가
    private User getUserByEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("인증된 사용자 이메일이 없습니다.");
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. email=" + email));
        if (user.getStatus() == UserStatus.WITHDRAWN) {
            throw new IllegalArgumentException("탈퇴한 회원입니다.");
        }
        return user;
    }
}
