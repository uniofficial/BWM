package com.bwm.wallet.service;

import java.util.List;
import com.bwm.wallet.dto.WalletHistoryResponseDto;

/**
 * 지갑 이력(WalletHistory) 관련 비즈니스 로직의 명세를 정의하는 인터페이스.
 */
public interface WalletHistoryService {
    
    // 포인트 변동 내역 조회
	List<WalletHistoryResponseDto> getMyHistory(String userUuid);
}
