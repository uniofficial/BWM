package com.bwm.wallet.entity;

public enum WalletHistoryType {
	CHARGE,       // 충전
    BID_PAYMENT,  // 입찰 결제
    REFUND,        // 환불
    SALES_REVENUE  // 판매 대금 정산 (경매 종료 시 입금)
}
