package com.bwm.wallet.service;

import com.bwm.wallet.dto.WalletResponseDto;
import com.bwm.user.entity.User;

public interface WalletService {

    // 사용자의 현재 잔액을 조회
    WalletResponseDto getMyWallet(String userEmail);

    // 사용자 포인트 충전
    void chargeUserPoint(Integer userId, Integer amount);

    // 포인트 차감

    void deductBidPoint(Integer userId, Integer itemId, Integer amount);

    // 최고 입찰자 경신 시 이전 최고 입찰자에게 포인트를 환불

    void refundBidPoint(Integer userId, Integer itemId, Integer amount);

<<<<<<< Updated upstream
    // 판매 대금 정산 (경매 종료 시 입금)
    void depositSalesRevenue(Integer sellerId, Integer itemId, Integer amount);
=======
    // 지갑 생성 (회원가입 시)
    void createWallet(com.bwm.user.entity.User user);
>>>>>>> Stashed changes
}
