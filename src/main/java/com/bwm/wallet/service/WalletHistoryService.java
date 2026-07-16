package com.bwm.wallet.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bwm.wallet.dto.WalletHistoryResponseDto;
import com.bwm.wallet.entity.WalletHistory;
import com.bwm.wallet.repository.WalletHistoryRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WalletHistoryService {
	
	private final WalletHistoryRepository walletHistoryRepository;
	
	public List<WalletHistoryResponseDto> getMyHistory(Integer userId){
		List<WalletHistory> histories = walletHistoryRepository.findByWalletUserIdOrderByCreatedAtDesc(userId);
		
		return histories.stream().map( history -> WalletHistoryResponseDto.builder()
																		  .type(history.getType().name())
																		  .itemId(history.getItemId())
																		  .amount(history.getAmount())
																		  .balanceAfter(history.getBalanceAfter())
																		  .createdAt(history.getCreatedAt())
																		  .build())
						.collect(Collectors.toList());
	}
}
