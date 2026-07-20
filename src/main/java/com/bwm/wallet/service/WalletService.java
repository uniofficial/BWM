package com.bwm.wallet.service;

import com.bwm.wallet.dto.WalletResponseDto;

public interface WalletService {
    
    // 사용자의 현재 잔액을 조회
    WalletResponseDto getMyWallet(Integer userId);
    
    // 사용자 포인트 충전
    void chargeUserPoint(Integer userId, Integer amount);
    
    // 포인트 차감
 
    void deductBidPoint(Integer userId, Integer itemId, Integer amount);
    
    // 최고 입찰자 경신 시 이전 최고 입찰자에게 포인트를 환불

    void refundBidPoint(Integer userId, Integer itemId, Integer amount);
}
