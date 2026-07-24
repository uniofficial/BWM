package com.bwm.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    
    // [상품 도메인: ITM]
    ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "ITM-001", "상품을 찾을 수 없습니다."),
    ITEM_STATE_CONFLICT(HttpStatus.CONFLICT, "ITM-002", "상품 상태 충돌이 발생했습니다."),
    ITEM_ACCESS_DENIED(HttpStatus.FORBIDDEN, "ITM-003", "상품 접근 권한이 없습니다."),

    // [지갑 도메인: WLT]
    WALLET_NOT_FOUND(HttpStatus.NOT_FOUND, "WLT-001", "지갑을 찾을 수 없습니다."),
    INVALID_AMOUNT(HttpStatus.BAD_REQUEST, "WLT-002", "유효하지 않은 금액입니다."),
    INSUFFICIENT_BALANCE(HttpStatus.BAD_REQUEST, "WLT-003", "잔액이 부족합니다."),

    // [입찰 도메인: BID]
    ALREADY_HIGHEST_BIDDER(HttpStatus.CONFLICT, "BID-001", "이미 최고 입찰자입니다."),
    BID_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "BID-002", "입찰할 상품을 찾을 수 없습니다."),
    SELLER_CANNOT_BID(HttpStatus.CONFLICT, "BID-003", "판매자는 자신의 상품에 입찰할 수 없습니다."),
    BID_NOT_OPEN(HttpStatus.CONFLICT, "BID-004", "입찰이 진행 중이 아닙니다."),
    BID_AUCTION_ENDED(HttpStatus.CONFLICT, "BID-005", "경매가 이미 종료되었습니다."),
    BID_AMOUNT_TOO_LOW(HttpStatus.BAD_REQUEST, "BID-006", "입찰 금액이 너무 낮습니다."),

    // [경매 도메인: AUC]
    AUCTION_ALREADY_CLOSED(HttpStatus.CONFLICT, "AUC-001", "경매가 이미 종료되었습니다."),
    AUCTION_PERMISSION_DENIED(HttpStatus.FORBIDDEN, "AUC-002", "경매 권한이 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
