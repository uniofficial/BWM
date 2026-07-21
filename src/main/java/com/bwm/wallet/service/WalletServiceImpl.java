package com.bwm.wallet.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bwm.item.entity.Item;
import com.bwm.item.repository.ItemRepository;
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
public class WalletServiceImpl implements WalletService {
    private final WalletRepository walletRepository;
    private final WalletHistoryRepository walletHistoryRepository;
    private final ItemRepository itemRepository;

    @Override
    public WalletResponseDto getMyWallet(Integer userId) {
        Wallet wallet = walletRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저의 지갑을 찾을 수 없습니다."));
        
        return new WalletResponseDto(wallet.getBalance());
    }

    @Override
    @Transactional
    public void chargeUserPoint(Integer userId, Integer amount) {
        Wallet wallet = walletRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저의 지갑을 찾을 수 없습니다."));
        
        wallet.charge(amount);
        
        WalletHistory history = WalletHistory.builder()
                                             .wallet(wallet)
                                             .item(null)
                                             .type(WalletHistoryType.CHARGE)
                                             .amount(amount)
                                             .balanceAfter(wallet.getBalance())
                                             .build();
        
        walletHistoryRepository.save(history);
    }

    @Override
    @Transactional
    public void deductBidPoint(Integer userId, Integer itemId, Integer amount) {
        Wallet wallet = walletRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저의 지갑을 찾을 수 없습니다."));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품을 찾을 수 없습니다."));

        wallet.deduct(amount);

        WalletHistory history = WalletHistory.builder()
                .wallet(wallet)
                .item(item)
                .type(WalletHistoryType.BID_PAYMENT)
                .amount(amount)
                .balanceAfter(wallet.getBalance())
                .build();

        walletHistoryRepository.save(history);
    }

    @Override
    @Transactional
    public void refundBidPoint(Integer userId, Integer itemId, Integer amount) {
        Wallet wallet = walletRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저의 지갑을 찾을 수 없습니다."));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품을 찾을 수 없습니다."));

        wallet.charge(amount);

        WalletHistory history = WalletHistory.builder()
                .wallet(wallet)
                .item(item)
                .type(WalletHistoryType.REFUND)
                .amount(amount)
                .balanceAfter(wallet.getBalance())
                .build();

        walletHistoryRepository.save(history);
    }
}
