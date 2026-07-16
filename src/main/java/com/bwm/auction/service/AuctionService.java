package com.bwm.auction.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bwm.auction.dto.AuctionCloseResponse;

import lombok.RequiredArgsConstructor;

/**
 * 경매 종료 비즈니스 로직
 */
@Service
@RequiredArgsConstructor
public class AuctionService {

    /*
     * TODO
     *
     * ItemRepository
     * UserRepository
     *
     * 병합 후 주입 예정
     */

    /**
     * 경매 종료
     *
     * @param itemId 상품 번호
     * @param requesterId 요청 사용자 번호
     */
    @Transactional
    public AuctionCloseResponse closeAuction(Long itemId, Long requesterId) {

        /*
         * 구현 예정
         *
         * 1. 상품 조회
         * 2. 판매자 본인 확인
         * 3. OPEN 상태 확인
         * 4. 마감시간 확인
         * 5. 최고 입찰자 존재 여부 확인
         * 6. SOLD / UNSOLD 처리
         * 7. 저장
         * 8. 응답 반환
         */

        throw new UnsupportedOperationException("아직 구현되지 않았습니다.");
    }

}