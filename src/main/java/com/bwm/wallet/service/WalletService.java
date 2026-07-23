package com.bwm.wallet.service;

import java.util.List;
import com.bwm.wallet.dto.WalletResponseDto;
import com.bwm.wallet.dto.WalletChargeRequestResponseDto;
import com.bwm.user.entity.User;

public interface WalletService {

    // 사용자의 현재 잔액을 조회
    WalletResponseDto getMyWallet(String userUuid);

    // 사용자 포인트 충전 요청 생성
    WalletChargeRequestResponseDto requestPointCharge(String userEmail, Integer amount);

    // 사용자 자신의 포인트 충전 요청 내역 조회
    List<WalletChargeRequestResponseDto> getMyChargeRequests(String userEmail);

    // 관리자용 모든 포인트 충전 요청 목록 조회
    List<WalletChargeRequestResponseDto> getAllChargeRequests();

    // 관리자 포인트 충전 요청 승인
    void approvePointCharge(Integer requestId);

    // 관리자 포인트 충전 요청 반려
    void rejectPointCharge(Integer requestId);

    // 사용자 포인트 직접 충전 (승인 시에도 내부 호출)
    void chargeUserPoint(Integer userId, Integer amount);

    // 포인트 차감
    void deductBidPoint(Integer userId, Integer itemId, Integer amount);

    // 최고 입찰자 경신 시 이전 최고 입찰자에게 포인트를 환불
    void refundBidPoint(Integer userId, Integer itemId, Integer amount);

    // 판매 대금 정산 (경매 종료 시 입금)
    void depositSalesRevenue(Integer sellerId, Integer itemId, Integer amount);

    // 지갑 생성 (회원가입 시)
    void createWallet(User user);
}
